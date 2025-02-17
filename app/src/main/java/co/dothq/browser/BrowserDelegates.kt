package co.dothq.browser

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import co.dothq.browser.managers.StorageManager
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession

class BrowserDelegates {
     fun createProgressDelegate(area: String, context: Context, applicationCtx: Context): GeckoSession.ProgressDelegate {
        return object : GeckoSession.ProgressDelegate {

            override fun onPageStop(session: GeckoSession, success: Boolean) = Unit

            override fun onSecurityChange(session: GeckoSession, securityInfo: GeckoSession.ProgressDelegate.SecurityInformation)  {

                if (area == "main") {
                    val activity: Activity = (context as Activity)
                    val contextualIdIcon = activity.findViewById<ImageView>(R.id.contextIdentityIcon);

                    if (securityInfo.isSecure) contextualIdIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_secure_filled))
                    if (!securityInfo.isSecure) contextualIdIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_unsecure_filled))
                    Toast.makeText(context, securityInfo.securityMode.toString(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onPageStart(session: GeckoSession, url: String) {

            }


            override fun onProgressChange(session: GeckoSession, progress: Int) {

            }
        }
    }

    public fun createNavigationDelegate(area: String, context: Context, applicationCtx: Context): GeckoSession.NavigationDelegate {
        return object : GeckoSession.NavigationDelegate {

            override fun onLocationChange(session: GeckoSession, url: String?) {
                super.onLocationChange(session, url)
                StorageManager().set(
                    applicationCtx,
                    "currentUri",
                    url.toString(),
                    "appValues"
                )

                val uri: Uri = url.toString().toUri();
                val host = uri.host.toString();
                val path = url.toString().replace("${uri.scheme}://${uri.host}", "");

                if (area == "main") {
                    val activity: Activity = (context as Activity)

                    activity.findViewById<TextView>(R.id.addressBarDomain).text =
                        host.toString();

                    if (path != "/") activity.findViewById<TextView>(R.id.addressBarPath).text = path
                    if (path == "/") activity.findViewById<TextView>(R.id.addressBarPath).text = ""
                }
            }

            override fun onLoadRequest(session: GeckoSession, request: GeckoSession.NavigationDelegate.LoadRequest): GeckoResult<AllowOrDeny>? {
                return (GeckoResult.allow())
            }
            }
        }
    }