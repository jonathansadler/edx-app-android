package org.edx.mobile.util

import android.text.TextUtils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.edx.mobile.BuildConfig
import java.util.*

/**
 * Created by rohan on 3/12/15.
 */
object ConfigUtil {

    /**
     * Returns true if domain of the given URL is white-listed in the configuration,
     * false otherwise.
     *
     * @param url
     * @return
     */
    fun isWhiteListedURL(url: String, config: Config): Boolean {
        // check if this URL is a white-listed URL, anything outside the white-list is EXTERNAL LINK
        for (domain in config.zeroRatingConfig.whiteListedDomains) {
            if (BrowserUtil.isUrlOfHost(url, domain)) {
                // this is white-listed URL
                return true
            }
        }
        return false
    }

    /**
     * Utility method to check the current build is white listed in Firebase remote config.
     *
     * @param config   [Config]
     * @param listener [OnWhiteListedReleaseListener] callback for the white listed release
     */
    fun isReleaseWhiteListed(config: Config,
                             listener: OnWhiteListedReleaseListener?) {
        if (config.firebaseConfig.isEnabled) {
            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

            //            TODO: // Need to remove from the pull request.
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(60)
                    .build()
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            //              TODO: // delete

            firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
                val courseUpgradeEnabled = firebaseRemoteConfig
                        .getBoolean(AppConstants.FirebaseConstants.REV_934_ENABLED)
                if (courseUpgradeEnabled) {
                    val whiteListedReleasesJson = firebaseRemoteConfig
                            .getString(AppConstants.FirebaseConstants.REV_934_WHITELISTED_RELEASES)
                    if (!TextUtils.isEmpty(whiteListedReleasesJson)) {
                        val whiteListReleases = Gson().fromJson<ArrayList<String>>(whiteListedReleasesJson,
                                object : TypeToken<ArrayList<String>>() {}.type)
                        for (WhiteListedRelease in whiteListReleases) {
                            if (BuildConfig.VERSION_NAME.equals(WhiteListedRelease, ignoreCase = true)) {
                                listener?.onWhiteListedRelease()
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Interface to provide the callback for white listed release.
     */
    interface OnWhiteListedReleaseListener {
        fun onWhiteListedRelease()
    }
}
