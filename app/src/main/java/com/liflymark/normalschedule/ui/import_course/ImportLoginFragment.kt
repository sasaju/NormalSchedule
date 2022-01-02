package com.liflymark.normalschedule.ui.import_course

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.MainActivity
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.databinding.FragmentImportLoginBinding
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.Dialog
import com.liflymark.normalschedule.ui.class_course.ClassCourseActivity
import com.liflymark.normalschedule.ui.import_again.ImportCourseAgain
import com.liflymark.normalschedule.ui.show_timetable.ShowTimetableActivity2
import es.dmoral.toasty.Toasty

class ImportLoginFragment: Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(CourseViewModel::class.java) }
    private var _binding: FragmentImportLoginBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    var userName = ""
    var userPassword = ""
    private var id = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentImportLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
//        return inflater.inflate(R.layout.fragment_import_login, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ImmersionBar.with(this).init()
        val waitDialog by lazy {  activity?.let { it1 -> Dialog.getProgressDialog(it1) } }
        if (viewModel.isAccountSaved()){
//            val intent = Intent(context, ImportScoreActivity::class.java).apply {
//                putExtra("isSaved", true)
//            }
            val intent = Intent(context, ShowTimetableActivity2::class.java).apply{
                putExtra("isSaved", true)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        if (activity is ImportCourseAgain){
            viewModel.accountLiveData.observe(viewLifecycleOwner){
                binding.user.text = SpannableStringBuilder(it[0])
                binding.password.text = SpannableStringBuilder(it[1])
            }
            viewModel.getAccount()
        }

        binding.selectSignMethod.attachDataSource(listOf("统一认证", "URP登陆"))
        binding.selectSignMethod.setOnSpinnerItemSelectedListener { parent, view, position, id ->
            when(position){
                0 -> {
                    binding.inputCode.visibility = View.INVISIBLE
                    binding.rlCode.visibility = View.INVISIBLE
                    binding.tipsText.visibility = View.VISIBLE
                    binding.etCode.setText("")
                }
                1 -> {
                    binding.inputCode.visibility = View.VISIBLE
                    binding.rlCode.visibility = View.VISIBLE
                    binding.tipsText.visibility = View.INVISIBLE
                }
            }
        }
        viewModel.getId()// 获取cookie
        viewModel.idLiveData.observe(viewLifecycleOwner, { result ->
            if (result == null || result.id == "") {
                binding.serverStatus.text = "目前可能仅允许“统一认证”登陆，如失败请尝试班级导入"
                binding.selectSignMethod.attachDataSource(listOf("统一认证"))
                id = ""
                binding.serverStatus.setTextColor(Color.RED)
            } else {
                this.id = result.id
                binding.serverStatus.text = "服务器正常"
            }
        })


        viewModel.courseLiveData.observe(viewLifecycleOwner, Observer { result ->

            if (result == null) {
                activity?.let { Toasty.error(it, "登陆异常，重启app试试", Toasty.LENGTH_SHORT).show() }
                waitDialog?.dismiss()
            } else {
                val allCourseList = result.allCourse
                when (result.status) {
                    "yes" -> {
                        saveAccount()
                        activity?.let {
                            Toasty.success(it, "登陆成功，解析成功", Toasty.LENGTH_SHORT).show()
                        }
//                        viewModel.insertOriginalCourse(allCourseList)
//                        for (singleCourse in allCourseList) {
//                            Log.d("ImportLoginFragment", singleCourse.toString())
//                        }
                        if (activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        if (activity is ImportCourseAgain) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        // viewModel.saveAccount(userName, userPassword)
                    }
                    "no" -> {
                        activity?.let {
                            Toasty.error(
                                it,
                                "登陆成功，解析异常，请务必检查课程表是否正确",
                                Toasty.LENGTH_LONG
                            ).show()
                        }
                        if (activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        // viewModel.saveAccount(userName, userPassword)
                    }
                    else -> {
                        activity?.let {
                            Toasty.error(it, result.status, Toasty.LENGTH_SHORT).show()
                        }
                        viewModel.getImage(id)
                        waitDialog?.dismiss()
                    }
                }


            }
        })

        viewModel.courseNewLiveData.observe(viewLifecycleOwner, Observer { result ->

            if (result == null) {
                activity?.let { Toasty.error(it, "登陆异常，将返回主界面", Toasty.LENGTH_SHORT).show() }
                waitDialog?.dismiss()
                Repository.saveLogin()
                val intent = Intent(context, ShowTimetableActivity2::class.java).apply{
                    putExtra("isSaved", true)
                }
                startActivity(intent)
                activity?.finish()
            } else {
                val allCourseList = result.allCourse
                when (result.status) {
                    "yes" -> {
                        saveAccount()
                        activity?.let {
                            Toasty.success(it, "登陆成功，解析成功", Toasty.LENGTH_SHORT).show()
                        }
//                        viewModel.insertOriginalCourse(allCourseList)
//                        for (singleCourse in allCourseList) {
//                            Log.d("ImportLoginFragment", singleCourse.toString())
//                        }
                        if (activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        if (activity is ImportCourseAgain) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        // viewModel.saveAccount(userName, userPassword)
                    }
                    "no" -> {
                        activity?.let {
                            Toasty.error(
                                it,
                                "登陆成功，解析异常，请务必检查课程表是否正确",
                                Toasty.LENGTH_LONG
                            ).show()
                        }
                        if (activity is MainActivity) {
                            val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                                putExtra("isSaved", false)
                                putExtra("courseList", Convert.allCourseToJson(allCourseList))
                                putExtra("user", userName)
                                putExtra("password", userPassword)
                            }
                            startActivity(intent)
                            activity?.finish()
                        }
                        // viewModel.saveAccount(userName, userPassword)
                    }
                    else -> {
                        activity?.let {
                            Toasty.error(it, result.status, Toasty.LENGTH_SHORT).show()
                        }
                        waitDialog?.dismiss()
                    }
                }
            }
        })

        viewModel.courseVisitLiveData.observe(viewLifecycleOwner, { result ->
            val course = result.getOrNull()

            if (course == null) {
                val allCourseList = listOf(
                    AllCourse(
                    "五四路",
                    1,
                    1,
                    "111111111111111111111111",
                    1,
                    "点击右上角导入课程",
                    "",
                    ""
                )
                )
                val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                    putExtra("isSaved", true)
                    putExtra("courseList", Convert.allCourseToJson(allCourseList))
                    putExtra("user", userName)
                    putExtra("password", userPassword)
                }
                startActivity(intent)
                activity?.finish()
            } else {
                val allCourseList = course.allCourse
                val intent = Intent(context, ShowTimetableActivity2::class.java).apply {
                    putExtra("isSaved", false)
                    putExtra("courseList", Convert.allCourseToJson(allCourseList))
                    putExtra("user", userName)
                    putExtra("password", userPassword)
                }
                startActivity(intent)
                activity?.finish()
            }
        })

        viewModel.imageLiveData.observe(viewLifecycleOwner, {
            binding.ivCode.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            if (it != null)
                binding.ivCode.setImageBitmap(it)
        })
        binding.ivCode.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getImage(id)
        }



        binding.btnSign.setOnClickListener {
            // 判断是否输入学号密码并提交数据至ViewModel层以更新数据
            userName = binding.user.text.toString()
            userPassword = binding.password.text.toString()
            waitDialog?.show()
//            dialog.Content()
//            dialog.showDialog.value = true
            val yzm = binding.etCode.text.toString()
            when {
                !binding.agreeOrNot.isChecked ->  activity?.let { it1 ->
                    Toasty.warning(it1, "您未同意用户协议", Toasty.LENGTH_SHORT).show()
                    waitDialog?.dismiss()
                }
                userName == "" -> activity?.let { it1 ->
                    Toasty.warning(it1, "请输入学号", Toasty.LENGTH_SHORT).show()
                    waitDialog?.dismiss()
                }
                userPassword == "" -> activity?.let { it1 ->
                    Toasty.warning(it1, "请输入密码", Toasty.LENGTH_SHORT).show()
                    waitDialog?.dismiss()
                }
                id == "" -> {
                    viewModel.putValue(userName, userPassword)
                }
                else -> viewModel.putValue(userName, userPassword, yzm, id)
            }
        }

        val contractDialog =
            Dialog.getContractDialog(
                requireContext(),
                yes = {
                    Toasty.info(requireContext(), "请点击登陆按钮上方的复选框以再次确认").show()
                },
                no = {
                    activity?.finish()
                }
            )
        val userContractDialog =
            Dialog.getUerContract(
                requireContext(),
                yes = {
                    Toasty.info(requireContext(), "请点击登陆按钮上方的复选框以再次确认").show()
                },
                no = {
                    activity?.finish()
                }
            )
        contractDialog.show()
        binding.contact.setOnClickListener {
            contractDialog.show()
        }
        binding.userContact.setOnClickListener {
            userContractDialog.show()
        }

        binding.btnSignByClass.setOnClickListener {
            val intent = Intent(context,ClassCourseActivity::class.java).apply {
                putExtra("allowImport", true)
            }

            if(binding.agreeOrNot.isChecked) {
                activity?.let { it1 ->
                    startActivity(intent)
                    it1.finish()
                }
            } else {
                activity?.let { it1 -> Toasty.info(it1,"您没有同意用户协议").show() }
            }
        }

        binding.btnSignByVisitor.setOnClickListener {
            if(binding.agreeOrNot.isChecked) {
                viewModel.putValue()
                viewModel.saveAccount("visit", "visit")
            } else {
                activity?.let { it1 -> Toasty.info(it1,"您没有同意用户协议").show() }
            }
        }
    }




    private fun saveAccount() {
        userName = binding.user.text.toString()
        userPassword = binding.password.text.toString()
        viewModel.saveAccount(userName, userPassword)
    }
}