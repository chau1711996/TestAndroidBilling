package dev.chau.testandroidbilling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import dev.chau.testandroidbilling.databinding.ActivityPurchaseBinding
import kotlinx.coroutines.*

class PurchaseActivity : AppCompatActivity(), PurchasesUpdatedListener {
    val TAG = PurchaseActivity::class.java.name
    private lateinit var billingClient: BillingClient
    private lateinit var lisner: ConsumeResponseListener

    private lateinit var binding: ActivityPurchaseBinding
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lisner = ConsumeResponseListener { billingResult, _ ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                Log.d(TAG, "ConsumeResponseListener OK")
            }
        }

        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(TAG, "onBillingSetupFinished")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d(TAG, "onBillingServiceDisconnected")
            }
        })

        init()
    }

    private fun init() {
        productAdapter = ProductAdapter {
            clickBuyItem(it)
        }

        binding.apply {
            val linearLayoutManager = LinearLayoutManager(binding.root.context)
            rcvItem.apply {
                setHasFixedSize(true)
                layoutManager = linearLayoutManager
                adapter = productAdapter
            }
            btnLoad.setOnClickListener {
                if (billingClient.isReady) {
                    GlobalScope.launch {
                        querySkuDetails()
                    }
                }
            }
        }
    }

    suspend fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add(GoldOneTime)
        skuList.add(GoldManyTime)
        skuList.add(MyGold)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        // leverage querySkuDetails Kotlin extension function
        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }
        productAdapter.submitList(skuDetailsResult.skuDetailsList)
        // Process the result.
    }

    private fun clickBuyItem(skuDetails: SkuDetails) {
        val purchaseParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        billingClient.launchBillingFlow(this, purchaseParams)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchase(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "USER_CANCELED")
        }
    }

    fun handlePurchase(list: List<Purchase>) {
        var text = ""
        list.forEach {
            //set cho product chỉ mua 1 lần
            //tự check token nếu đã sử dụng rồi thì sẽ k hiện nữa
            if (it.skus.equals(GoldOneTime)) {
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                    .build()
                billingClient.consumeAsync(consumeParams, lisner)
            }
            text += "\\n" + it.skus + "\\n"
        }
        binding.txtShowPurchase.text = text
    }

    companion object {
        const val GoldOneTime = "gold_one_time"
        const val GoldManyTime = "gold_many_time"
        const val MyGold = "my_gold"
    }
}