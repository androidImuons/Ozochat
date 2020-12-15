package com.ozonetech.ozochat.view.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.ozonetech.ozochat.R
import com.ozonetech.ozochat.databinding.FragmentUserStatusBinding
import com.ozonetech.ozochat.utils.MyPreferenceManager
import com.ozonetech.ozochat.view.activity.StatusEditActivity
import com.ozonetech.ozochat.view.adapter.UserStatusAdapter
import java.util.*


class UserStatusFragment : Fragment() {

    private lateinit var binding : FragmentUserStatusBinding
    private lateinit var myPreferenceManager : MyPreferenceManager
    private val requestCodePicker = 100
    private lateinit var myAdapter: UserStatusAdapter
    private lateinit var options: Options
    private var returnValue = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_status, container, false)
        val view: View = binding.getRoot()
        binding.setLifecycleOwner(this)
        myPreferenceManager = MyPreferenceManager(activity)
        renderView()
        return view
    }

    private fun renderView() {
        if (myPreferenceManager.userDetails[MyPreferenceManager.KEY_PROFILE_PIC] != null) {
            Log.d("image url", "--details-" + myPreferenceManager.userDetails[MyPreferenceManager.KEY_PROFILE_PIC])
            Glide.with(activity!!)
                    .load(myPreferenceManager.userDetails[MyPreferenceManager.KEY_PROFILE_PIC])
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.person_icon)
                    .into(binding.thumbnail)
        }

        binding.rvRecentUpdates.layoutManager = LinearLayoutManager(activity)
        myAdapter = UserStatusAdapter(requireContext())
        options = Options.init()
                .setRequestCode(requestCodePicker)
                .setCount(5)
                .setPreSelectedUrls(returnValue)
                .setExcludeVideos(false)
                .setVideoDurationLimitinSeconds(30)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/akshay/new")
        binding.rvRecentUpdates.adapter = myAdapter

        binding.llMyStatus.setOnClickListener {
            options.preSelectedUrls = returnValue
            Pix.start(this@UserStatusFragment, options)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestCodePicker -> {
                if (resultCode == Activity.RESULT_OK) {
                    returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!

                    gotoImgEditor(returnValue)
                    /* val bundle = Bundle()
                    bundle.putSerializable("KEY_ARRAYLIST", returnValue)
                    val mapFragment = AddImageWithCaptionFragment()
                    mapFragment.arguments = bundle*/

                   //  myAdapter.addImage(returnValue)
                }
            }
        }
    }

    private fun gotoImgEditor(returnValue: ArrayList<String>) {
        val intent = Intent(activity, StatusEditActivity::class.java)
        intent.putExtra("key", returnValue);
        startActivity(intent)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, options)
                } else {
                    Toast.makeText(activity, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): UserStatusFragment {
            val fragment = UserStatusFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}