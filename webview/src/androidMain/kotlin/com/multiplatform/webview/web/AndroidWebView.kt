package com.multiplatform.webview.web

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.multiplatform.webview.jsbridge.JsBridge
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.util.KLogger
import kotlinx.coroutines.CoroutineScope

/**
 * Created By Kevin Zou On 2023/9/5
 */

/**
 * Android implementation of [IWebView]
 */
class AndroidWebView(
    private val webView: WebView,
    override var scope: CoroutineScope,
    override var jsBridge: JsBridge,
) : IWebView {
    init {
        initWebView()
    }
    override fun canGoBack() = webView.canGoBack()

    override fun canGoForward() = webView.canGoForward()

    override fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String, String>,
    ) {
        webView.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadHtml(
        html: String?,
        baseUrl: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?,
    ) {
        if (html == null) return
        webView.loadDataWithBaseURL(baseUrl, html, mimeType, encoding, historyUrl)
    }

    override suspend fun loadHtmlFile(fileName: String) {
        KLogger.d {
            "loadHtmlFile: $fileName"
        }
        webView.loadUrl("file:///android_asset/$fileName")
    }

    override fun postUrl(
        url: String,
        postData: ByteArray,
    ) {
        webView.postUrl(url, postData)
    }

    override fun goBack() {
        webView.goBack()
    }

    override fun goForward() {
        webView.goForward()
    }

    override fun reload() {
        webView.reload()
    }

    override fun stopLoading() {
        webView.stopLoading()
    }

    override fun evaluateJavaScript(
        script: String,
        callback: ((String) -> Unit)?,
    ) {
        val androidScript = "javascript:$script"
        KLogger.i {
            "evaluateJavaScript: $androidScript"
        }
        webView.post {
            webView.evaluateJavascript(androidScript, callback)
        }
    }

    override fun injectBridge(jsBridge: JsBridge) {
        webView.addJavascriptInterface(this, "jsBridge")
    }

    @JavascriptInterface
    fun call(
        id: Int,
        method: String,
        params: String,
    ) {
        KLogger.d { "call from JS: $id, $method, $params" }
        jsBridge.dispatch(JsMessage(id, method, params))
    }
}
