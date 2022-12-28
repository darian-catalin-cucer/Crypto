package cucerdariancatalin.crypto

internal object GFG {
    fun timeTakenToType(keyPad: String, word: String) {
        val pos = IntArray(26)
        for (i in 0..9) {
            val ch = keyPad[i]
            pos[ch - 'a'] = i
        }
        var last = 0
        var result = 0
        for (i in 0 until word.length) {
            val ch = word[i]
            val destination = pos[ch - 'a']
            val distance = Math.abs(destination - last)
            result += distance
            last = destination
        }
        println(result)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val keyPad = "acdbefghijlkmnopqrtsuwvxyz"
        val word = "dog"
        timeTakenToType(keyPad, word)
    }
}