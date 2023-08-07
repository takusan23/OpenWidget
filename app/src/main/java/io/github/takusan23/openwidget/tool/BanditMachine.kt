package io.github.takusan23.openwidget.tool

import io.github.takusan23.openwidget.app.UsageStatusData
import kotlin.random.Random

/** バンディットマシーンにアプリを選んでもらう */
object BanditMachine {

    /** 活用を行う割合 */
    private const val EPSILON = 0.7f

    /**
     * バンディットマシーンを回してアプリ一覧を返してもらう
     *
     * @param machineList アプリ一覧
     * @param playCount 何回アームを回すか。返り値の配列のサイズになる
     * @return 起動しそうなアプリ
     */
    fun playAndResultAppList(machineList: List<UsageStatusData>, playCount: Int): List<UsageStatusData> {
        var originList = machineList
        return buildList {
            // 回数回す
            repeat(playCount) {
                val pickData = if (isNextFind()) {
                    // 捜索をする。適当に
                    originList.random()
                } else {
                    // 活用を行う。起動率の高いものを選ぶ
                    originList.maxBy { it.foregroundUsageTimeMs }
                }
                // 結果を入れる。あと二回目を引かないように消す
                this += pickData
                originList = (originList - pickData)
            }
        }
    }

    /** 次の行動が捜索の場合は true */
    private fun isNextFind(): Boolean = EPSILON < Random.nextFloat()

}