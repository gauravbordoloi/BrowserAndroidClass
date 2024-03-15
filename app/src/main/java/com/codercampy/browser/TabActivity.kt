package com.codercampy.browser

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Explode
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintSet
import com.codercampy.browser.databinding.ActivityTabBinding
import com.codercampy.browser.databinding.LayoutOverflowMenuBinding


class TabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url")
        val openKeyboard = intent.getBooleanExtra("open_keyboard", false)

        if (!url.isNullOrEmpty()) {
            loadUrl(url)
        } else if (openKeyboard) {
            binding.editText.requestFocus()
        } else {
            loadHome()
        }

        initWebView()
        initBackPress()
        initEditText()
        initKeyboardGo()
        initButtons()
    }

    private fun loadHome() {
        binding.webView.clearHistory()
        binding.webView.loadUrl("https://google.com")
    }

    private fun loadUrl(url: String) {
        binding.webView.clearHistory()
        binding.webView.loadUrl(url)
    }

    private fun initButtons() {
        binding.btnHome.setOnClickListener {
            loadHome()
        }

        binding.btnShare.setOnClickListener {
            shareCurrentUrl()
        }

        binding.btnOverflowMenu.setOnClickListener {
            showOverflowMenu(it)
        }
    }

    private fun showOverflowMenu(view: View) {
        val popupWindow = PopupWindow(
            view,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            true
        )
        val menuBinding = LayoutOverflowMenuBinding.inflate(LayoutInflater.from(this))
        if (!binding.webView.canGoForward()) {
            menuBinding.btnForward.isEnabled = false
        }


        popupWindow.contentView = menuBinding.root
        popupWindow.showAsDropDown(view)

        menuBinding.btnForward.setOnClickListener {
            if (binding.webView.canGoForward()) {
                binding.webView.goForward()
                popupWindow.dismiss()
            }
        }
    }

    private fun shareCurrentUrl() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, binding.webView.url)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun initKeyboardGo() {
        binding.editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val url = binding.editText.text?.toString()?.trim()
                if (!url.isNullOrEmpty()) {

                    if (isValidUrl(url)) {
                        binding.webView.loadUrl(url)
                    } else {
                        binding.webView.loadUrl("https://www.google.com/search?q=$url")
                    }

                }

                hideKeyboard(binding.editText)
                minimiseEditText()
                hideCursor()
            }
            return@setOnEditorActionListener true
        }
    }

    private fun hideCursor() {
        binding.editText.isCursorVisible = false
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            Uri.parse(url).scheme == "https"
        } catch (e: Exception) {
            false
        }
    }

    private fun initEditText() {
        binding.editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && v.hasFocus()) {
                //maximise the edit text
                binding.editText.isCursorVisible = true
                maximiseEditText()
            } else if (!v.hasFocus()) {
                minimiseEditText()
                binding.editText.setText(binding.webView.url)
            }
        }
    }

    private fun maximiseEditText() {
        TransitionManager.beginDelayedTransition(binding.toolbar, ChangeBounds())

        ConstraintSet().apply {
            clone(binding.toolbar)

            //modify the constraints
            connect(
                binding.editText.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                dpToPx(16)
            )

            connect(
                binding.editText.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                dpToPx(16)
            )

            applyTo(binding.toolbar)
        }

        TransitionManager.beginDelayedTransition(binding.toolbar, Explode())

        binding.btnHome.visibility = View.GONE
        binding.btnShare.visibility = View.GONE
        binding.btnOverflowMenu.visibility = View.GONE
    }

    private fun minimiseEditText() {
        TransitionManager.beginDelayedTransition(binding.toolbar, ChangeBounds())

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.toolbar)

        //modify the constraints
        constraintSet.connect(
            binding.editText.id,
            ConstraintSet.START,
            binding.btnHome.id,
            ConstraintSet.END
        )

        constraintSet.connect(
            binding.editText.id,
            ConstraintSet.END,
            binding.btnShare.id,
            ConstraintSet.START
        )

        constraintSet.applyTo(binding.toolbar)

        TransitionManager.beginDelayedTransition(binding.toolbar, Explode())

        binding.btnHome.visibility = View.VISIBLE
        binding.btnShare.visibility = View.VISIBLE
        binding.btnOverflowMenu.visibility = View.VISIBLE
    }

    private fun initBackPress() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun initWebView() {
        //Initialize webview
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE

                //set the url in the toolbar
                binding.editText.setText(url)
                //set the ssl icon
                if (view.certificate != null) {
                    binding.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_lock, 0, 0, 0
                    )
                } else {
                    binding.editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
                }

            }

        }
    }

}