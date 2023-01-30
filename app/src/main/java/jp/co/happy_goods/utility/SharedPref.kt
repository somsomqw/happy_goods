package jp.co.happy_goods.utility

import android.content.Context

object SharedPref {

    //login email 保存
    fun getLoginEmail (context: Context?): String? {return getSharedPreference(context, Constants.SHARED_PREFERENCES_LOGIN_EMAIL) }
    fun updateLoginEmail(context: Context?, mobileTelNoRef: String) {setSharedPreference(context, Constants.SHARED_PREFERENCES_LOGIN_EMAIL, mobileTelNoRef)}

    /**
     * Local保存データの取得
     */
    private fun getSharedPreference(context: Context?, type: String): String? {
        val preferences =
            context?.getSharedPreferences(Constants.SHARED_PREFERENCES_INFO, Context.MODE_PRIVATE)
        return preferences?.getString(type, "")
    }

    /**
     * デバイス情報のLocal保存
     */
    private fun setSharedPreference(context: Context?, type: String?, info: String?) {
        val sharedPreferences =
            context?.getSharedPreferences(Constants.SHARED_PREFERENCES_INFO, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString(type, info)
        editor?.apply()
    }
}