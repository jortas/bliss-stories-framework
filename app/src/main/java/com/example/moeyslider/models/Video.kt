package com.example.moeyslider.models

import android.net.Uri


//All length are in MS
data class Video(
    val link: Uri,
    val thumbnail: Uri,
    val length: Int
) {

    companion object {
        const val MIN_LENGTH = 5000
        const val MAX_LENGTH = 10000
    }
}

data class Story(val video: Video)

fun storyFactoryMock(numberOfStories: Int = 5): MutableList<Story> {
    val storyList = mutableListOf<Story>()
    for (i in 0 until numberOfStories) {
        val story = Story(
            Video(
                link = Uri.parse(VIDEO1),
                thumbnail = Uri.parse(THUMB1),
                length = (Video.MIN_LENGTH + Math.random() * (Video.MAX_LENGTH - Video.MIN_LENGTH)).toInt()
            )
        )
        storyList.add(story)
    }
    return storyList
}


const val VIDEO1 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
const val THUMB1 = "https://doc-0o-1c-docs.googleusercontent.com/docs/securesc/7susn09vl6bta257ns173c560hdgo4nh/l9o141ks1pr7d8ldolju6hel7gnpeqnb/1643290425000/02353941631099677339/02353941631099677339/17RW9jETdZGKiba-HbtUqOzj-2KF-A7JY?e=download&authuser=0&nonce=jgju7kf3vs194&user=02353941631099677339&hash=2m0qf048kd08vjp58h4fovsi0bolmce3"