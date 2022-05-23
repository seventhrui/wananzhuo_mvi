/*
 * Tencent is pleased to support the icon_right_arrow source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seventh.demo.widget.qmuirefresh


/**
 * RefreshView 永远和 TargetView 保持一定的距离(这个距离由刷新时RefreshView居中算出)
 *
 * @author cginechen
 * @date 2017-06-07
 */

class QMUIFollowRefreshOffsetCalculator : QMUIPullRefreshLayout.RefreshOffsetCalculator {

    override fun calculateRefreshOffset(
        refreshInitOffset: Int,
        refreshEndOffset: Int,
        refreshViewHeight: Int,
        targetCurrentOffset: Int,
        targetInitOffset: Int,
        targetRefreshOffset: Int
    ): Int {
        val distance = targetRefreshOffset / 2 + refreshViewHeight / 2
        val max = targetCurrentOffset - refreshViewHeight
        return Math.min(max, targetCurrentOffset - distance)
    }
}
