package edu.buptsse.youxuancheng.network

data class Response(
    var code: Int,
    var result: String
) {
    fun error(): Boolean {
        return code > 250
    }
}
