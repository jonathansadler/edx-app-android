package org.edx.mobile.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_payments_info.*
import org.edx.mobile.R
import org.edx.mobile.base.BaseFragment
import org.edx.mobile.databinding.FragmentPaymentsInfoBinding
import org.edx.mobile.model.api.CourseUpgradeResponse
import org.edx.mobile.model.api.EnrolledCoursesResponse
import org.edx.mobile.util.DateUtil
import org.edx.mobile.util.ResourceUtil
import java.text.SimpleDateFormat
import java.util.*

class PaymentsInfoFragment : BaseFragment() {
    companion object {
        fun newInstance(extras: Bundle): PaymentsInfoFragment {
            val fragment = PaymentsInfoFragment()
            fragment.arguments = extras
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentPaymentsInfoBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_payments_info, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = view.context
        btn_close.setOnClickListener { activity?.finish() }
        val courseData = arguments?.getSerializable(Router.EXTRA_COURSE_DATA) as EnrolledCoursesResponse

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val expiryDate = DateUtil.convertToDate(courseData.auditAccessExpires)
        // TODO: Update following date
        val upgradeBy = DateUtil.convertToDate(courseData.auditAccessExpires)

        tv_audit_access_expires_on.text = ResourceUtil.
                getFormattedString(context.resources, R.string.audit_access_expires_on, "date",
                        dateFormat.format(expiryDate))
        val params: MutableMap<String, CharSequence> = HashMap()
        params["expiry_date"] = dateFormat.format(expiryDate)
        params["upgrade_by"] = dateFormat.format(upgradeBy)
        tv_audit_access_expires_details.text = ResourceUtil.
                getFormattedString(context.resources, R.string.audit_access_expires_details, params)

        val courseUpgradeData = arguments?.getSerializable(Router.EXTRA_COURSE_UPGRADE_DATA) as CourseUpgradeResponse
        PaymentsBannerFragment.loadPaymentsBannerFragment(R.id.fragment_container, courseData,
                courseUpgradeData, false, childFragmentManager, false)
    }
}
