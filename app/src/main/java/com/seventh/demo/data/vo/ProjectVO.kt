package com.seventh.demo.data.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProjectListVo(
    val datas: List<ProjectVO>
):Parcelable

@Parcelize
data class ProjectVO(
    val audit: Int,
    val author: String,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    val host: String,
    val id: Int,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: ArrayList<TagVO>,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
): Parcelable
