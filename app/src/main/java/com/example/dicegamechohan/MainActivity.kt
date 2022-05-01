package com.example.dicegamechohan

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    // ゲーム内部の変数定義
    // ユーザーのクレジット値の設定
    private var USER_CREDITS = 1000
    private val ALTER_CREDITS_INTERVAL = 100
    // 選択時のステータス設定
    private val SEL_BTN_CODE_CHOU = 0
    private val SEL_BTN_CODE_HAN = 1
    // ゲーム終了の基準値設定
    private val GAME_END_WIN = 2000
    private val GAME_END_LOSE = 0
    // サイコロ定義
    private var diceOne = Dice()
    private var diceTwo = Dice()

    // 共有レイアウト
    //メッセージ系
    private var comMessageArea: TextView? = null
    private var userCreditBoard: TextView? = null
    //画像系
    private var diceImageOne: ImageView? = null
    private var diceImageTwo: ImageView? = null
    private var topWomenGambler: ImageView? = null
    //ボタン系
    private var selBtnLeft: Button? = null
    private var selBtnRight: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //クレジット残高の初期化
        comMessageArea = findViewById(R.id.chouhan_explaion)
        userCreditBoard = findViewById(R.id.chouhan_credits)

        //ゲーム開始時のレイアウト一括定義
        initializeDefaultLayout()

        //ゲーム開始時の初期化
        initializeDefaultStatus()

    }

    /**
     *  ゲーム開始時における、各種レイアウトの定義
     */
    private fun initializeDefaultLayout(){
        //各種レイアウトの定義

        //サイコロ
        diceImageOne = findViewById(R.id.dice_one)
        diceImageTwo = findViewById(R.id.dice_two)

        //賭博子
        topWomenGambler = findViewById(R.id.chouhan_top)

        //各種ボタン（丁・半・再戦）
        selBtnLeft = findViewById(R.id.sel_btn_chou)
        selBtnRight = findViewById(R.id.sel_btn_han)
    }

    /**
     * ゲーム開始時の状態初期化
     */
    private fun initializeDefaultStatus(){
        // ユーザーのクレジット残高とメッセージを初期化
        USER_CREDITS = 1000
        userCreditBoard?.text = USER_CREDITS.toString()
        comMessageArea?.text = getString(R.string.layout_default_msg)

        // サイコロを一時的に隠す
        diceImageOne?.visibility = ImageView.INVISIBLE
        diceImageTwo?.visibility = ImageView.INVISIBLE

        //賭場子のイメージを更新
        topWomenGambler?.setImageResource(R.drawable.chouhan_default)

        //丁ボタンの押下時のリスナー定義
        selBtnLeft?.text = getString(R.string.select_btn_name_chou)
        selBtnLeft!!.setOnClickListener {
            playChouOrHanGame(SEL_BTN_CODE_CHOU)
        }

        //半ボタンの押下のリスナー定義
        selBtnRight?.text = getString(R.string.select_btn_name_han)
        selBtnRight!!.setOnClickListener {
            playChouOrHanGame(SEL_BTN_CODE_HAN)
        }

        //賭博子の画像をロングタップ時のリスナー定義
        topWomenGambler?.setOnLongClickListener {
            showSnackbar(getString(R.string.com_rule_msg))
        }
    }

    /**
     * 丁・半の判定
     */
    private fun playChouOrHanGame(BTN_STATUS: Int){

        //さいころを振る
        val diceOne = rollTheDiceOne()
        val diceTwo = rollTheDiceTwo()

        // サイコロの目の合計から、丁・半を判定
        val numOfDiceMod = (diceOne + diceTwo) % 2

        //押したボタンで処理を分岐する
        when(BTN_STATUS){
            //「丁」を選択（偶数）
            SEL_BTN_CODE_CHOU -> {
                // 結果表示
                when(numOfDiceMod) {
                    0 -> {
                        // ユーザーの勝ち（+100）
                        comMessageArea?.text = getString(R.string.layout_you_win_msg)
                        USER_CREDITS += ALTER_CREDITS_INTERVAL
                    }
                    else -> {
                        // ユーザーの負け（-100)
                        comMessageArea?.text = getString(R.string.layout_you_lose_msg)
                        USER_CREDITS -= ALTER_CREDITS_INTERVAL
                    }

                }//<< when
            }//<< SEL_BTN_CODE_CHOU

            //「半」を選択（奇数）
            SEL_BTN_CODE_HAN -> {
                // 結果表示
                when(numOfDiceMod) {
                    0 -> {
                        // ユーザーの負け（-100)
                        comMessageArea?.text = getString(R.string.layout_you_lose_msg)
                        USER_CREDITS -= ALTER_CREDITS_INTERVAL
                    }
                    else -> {
                        // ユーザーの勝ち（+100）
                        comMessageArea?.text = getString(R.string.layout_you_win_msg)
                        USER_CREDITS += ALTER_CREDITS_INTERVAL
                    }
                }
            }//<< SEL_BTN_CODE_HAN
        }//<< when

        // サイコロを表示
        diceImageOne?.visibility = ImageView.VISIBLE
        diceImageTwo?.visibility = ImageView.VISIBLE

        //クレジットの更新
        userCreditBoard?.text = USER_CREDITS.toString()

        //実施結果の更新
        refreshDisplayGameEnd()
    }

    /**
     * サイコロを振る処理（サイコロ１）
     */
    private fun rollTheDiceOne(): Int{
        //さいころを振る
        val numOfDiceOne = diceOne.rollDice()

        // 振った目のサイコロ画像を取得
        val drawbleResource = when(numOfDiceOne) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        // 画面更新
        diceImageOne?.setImageResource(drawbleResource)

        // 振った目を返す
        return numOfDiceOne
    }

    /**
     * サイコロを振る処理（サイコロ２）
     */
    private fun rollTheDiceTwo(): Int{
        //さいころを振る
        val numOfDiceTwo = diceTwo.rollDice()

        // 振った目のサイコロ画像を取得
        val drawbleResource = when(numOfDiceTwo) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        // 画面更新
        diceImageTwo?.setImageResource(drawbleResource)

        // 振った目を返す
        return numOfDiceTwo
    }

    /**
     * ゲーム結果を更新
     */
    private fun refreshDisplayGameEnd(){
        //ゲームの結果
        if(USER_CREDITS >= GAME_END_WIN) {
            //ユーザーの勝利
            comMessageArea?.text = getString(R.string.game_finish_you_win_msg)
            topWomenGambler?.setImageResource(R.drawable.chohan_you_win)

            showSnackbar(getString(R.string.com_you_win_snack_msg))
            changeLayoutGameEnd()

        } else if(USER_CREDITS <= GAME_END_LOSE ){
            //ユーザーの敗北
            comMessageArea?.text = getString(R.string.game_finish_you_lose_msg)
            topWomenGambler?.setImageResource(R.drawable.chohan_you_lose)

            showSnackbar(getString(R.string.com_you_lose_snack_msg))
            changeLayoutGameEnd()
        } else {
            //do nothing
            return
        }
    }

    /**
     * ゲーム終了時の画面
     */
    private fun changeLayoutGameEnd(){

        //賭場子をタップ時のメッセージを変更
        topWomenGambler?.setOnLongClickListener {
            showSnackbar(getString(R.string.com_game_end_msg))
        }

        // ボタンのテキスト更新（再戦）
        selBtnLeft?.text = getString(R.string.select_btn_name_restart)
        selBtnLeft?.setOnClickListener {
            initializeDefaultStatus()
        }
        
        // ボタンのテキスト更新（終了）
        selBtnRight?.text = getString(R.string.select_btn_name_finish)
        selBtnRight?.setOnClickListener {
            finish()
        }
    }

    /**
     * 賭場子押下時のヘルプメッセージの表示
     * 
     * @param showMessage 表示するメッセ―ジ
     */
    private fun showSnackbar(showMessage: String): Boolean {
        //
        Snackbar.make(
            findViewById(R.id.chohan_constraint_layout),
            showMessage,
            Snackbar.LENGTH_LONG
        ).show()
        return true
    }
    
}

/**
 * サイコロのクラス
 */
class Dice {
    
    // １～６の面いずれかをランダムで返す
    fun rollDice(): Int {
        return (1..6).random()
    }
}