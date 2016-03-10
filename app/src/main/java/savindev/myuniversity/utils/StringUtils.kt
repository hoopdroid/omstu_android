package savindev.myuniversity.utils

object StringUtils {
    fun editdist(S1: String, S2: String): Int {
        val m = S1.length
        val n = S2.length
        var D1: IntArray
        var D2 = IntArray(n + 1)

        for (i in 0..n)
            D2[i] = i

        for (i in 1..m) {
            D1 = D2
            D2 = IntArray(n + 1)
            for (j in 0..n) {
                if (j == 0)
                    D2[j] = i
                else {
                    val cost = if (S1[i - 1] != S2[j - 1]) 1 else 0
                    if (D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost)
                        D2[j] = D2[j - 1] + 1
                    else if (D1[j] < D1[j - 1] + cost)
                        D2[j] = D1[j] + 1
                    else
                        D2[j] = D1[j - 1] + cost
                }
            }
        }
        return D2[n]
    }
}
