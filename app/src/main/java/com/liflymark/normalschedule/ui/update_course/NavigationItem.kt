package com.liflymark.normalschedule.ui.update_course

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(var pagerCount:Int, var icon:ImageVector, var title:String){
    object Home: NavigationItem(0, Icons.Outlined.Home, "调课页")
    object Upload :NavigationItem(1, Icons.Outlined.Upload, "上传")
    object Profile: NavigationItem(2, Icons.Outlined.PermIdentity,"我的")
}
