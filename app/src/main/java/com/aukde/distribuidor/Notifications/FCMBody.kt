package com.aukde.distribuidor.Notifications

data class FCMBody ( val to: String = "",
                     val priority: String = "",
                     var data: Map<String, String>
)


