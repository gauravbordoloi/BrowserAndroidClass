package com.codercampy.browser

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codercampy.browser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeItems = mutableListOf<Any>()
        homeItems.add(getHomeStarredItem())
        homeItems.addAll(getDummyArticles())
        homeItems.add(getHomeStarredItem())
        homeItems.addAll(getDummyArticles())

        val adapter = HomeAdapter(homeItems, object : HomeAdapterListener {
            override fun onArticleClick(articleItem: HomeArticleItem) {
                loadPage(articleItem.link)
            }

            override fun onArticleShare(articleItem: HomeArticleItem) {
                shareArticle(this@MainActivity, articleItem)
            }

            override fun onStarredClick(link: String) {

            }

            override fun onSearchBarClick() {
                openTabActivityWithKeyboard()
            }

            override fun onMicClick() {

            }

            override fun onCameraClick() {

            }
        })
        binding.recyclerView.adapter = adapter

    }

    private fun openTabActivityWithKeyboard() {
        val intent = Intent(this, TabActivity::class.java)
        intent.putExtra("open_keyboard", true)
        startActivity(intent)
    }

    private fun getHomeStarredItem(): HomeStarredItem {
        return HomeStarredItem(
            "Facebook",
            "https://img.freepik.com/premium-vector/blue-social-media-logo_197792-1759.jpg",
            "Facebook",
            "https://img.freepik.com/premium-vector/blue-social-media-logo_197792-1759.jpg",
            "Facebook",
            "https://img.freepik.com/premium-vector/blue-social-media-logo_197792-1759.jpg",
            "Facebook",
            "https://img.freepik.com/premium-vector/blue-social-media-logo_197792-1759.jpg"
        )
    }

    private fun loadPage(url: String? = null) {
        val intent = Intent(this, TabActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun getDummyArticles(): List<HomeArticleItem> {
        val list = mutableListOf<HomeArticleItem>()
        for (i in 0 until 10) {
            list.add(
                HomeArticleItem(
                    "Elon Musk vs. Sam Altman: Who is winning the battle",
                    "https://c.biztoc.com/p/81998e685902c62d/og.webp",
                    "https://biztoc.com/x/81998e685902c62d",
                    System.currentTimeMillis()
                )
            )
        }
        return list
    }

}