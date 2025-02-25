package com.example.unipiaudiostories.utils

fun transformGoogleDriveUrl(url: String): String {
    val regex = Regex("https://drive.google.com/file/d/([^/]+)/?.*")
    val match = regex.find(url)
    return match?.groupValues?.get(1)?.let { fileId ->
        "https://drive.google.com/uc?export=view&id=$fileId"
    } ?: url // Return original URL if not a Google Drive link
}
