package org.edx.mobile.view

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.inject.Inject
import kotlinx.android.synthetic.main.fragment_payments_banner.*
import org.edx.mobile.R
import org.edx.mobile.base.BaseFragment
import org.edx.mobile.core.IEdxEnvironment
import org.edx.mobile.databinding.FragmentPaymentsBannerBinding
import org.edx.mobile.model.api.CourseUpgradeResponse
import org.edx.mobile.model.api.EnrolledCoursesResponse

class PaymentsBannerFragment : BaseFragment() {
    @Inject
    var environment: IEdxEnvironment? = null

    companion object {
        private const val EXTRA_SHOW_INFO_BUTTON = "show_info_button"
        private fun newInstance(courseData: EnrolledCoursesResponse, courseUpgradeData: CourseUpgradeResponse,
                                showInfoButton: Boolean): Fragment {
            val fragment = PaymentsBannerFragment()
            val bundle = Bundle()
            bundle.putSerializable(Router.EXTRA_COURSE_DATA, courseData)
            bundle.putSerializable(Router.EXTRA_COURSE_UPGRADE_DATA, courseUpgradeData)
            bundle.putBoolean(EXTRA_SHOW_INFO_BUTTON, showInfoButton)
            fragment.arguments = bundle
            return fragment
        }

        fun loadPaymentsBannerFragment(containerId: Int, courseData: EnrolledCoursesResponse,
                                       courseUpgradeData: CourseUpgradeResponse, showInfoButton: Boolean,
                                       childFragmentManager: FragmentManager) {
            val fragment: Fragment = newInstance(courseData, courseUpgradeData, showInfoButton)
            // This activity will only ever hold this lone fragment, so we
            // can afford to retain the instance during activity recreation
            fragment.retainInstance = true
            val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
            fragmentTransaction.add(containerId, fragment)
            fragmentTransaction.disallowAddToBackStack()
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentPaymentsBannerBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_payments_banner, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateCourseUpgradeBanner(view.context)
    }

    private fun populateCourseUpgradeBanner(context: Context) {
        val courseUpgradeData: CourseUpgradeResponse =
                arguments?.getSerializable(Router.EXTRA_COURSE_UPGRADE_DATA) as CourseUpgradeResponse
        val courseData: EnrolledCoursesResponse =
                arguments?.getSerializable(Router.EXTRA_COURSE_DATA) as EnrolledCoursesResponse
        val showInfoButton: Boolean = arguments?.getBoolean(EXTRA_SHOW_INFO_BUTTON) ?: false
        upgrade_to_verified_footer.visibility = View.VISIBLE
        if (showInfoButton) {
            info.visibility = View.VISIBLE
            info.setOnClickListener {
                environment?.router?.showPaymentsInfoActivity(context, courseData, courseUpgradeData)
            }
        } else {
            info.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(courseUpgradeData.getPrice())) {
            tv_upgrade_price.text = courseUpgradeData.price
        } else {
            tv_upgrade_price.visibility = View.GONE
        }
        ll_upgrade_button.setOnClickListener {
            environment?.router?.showCourseUpgradeWebViewActivity(context, courseUpgradeData.basketUrl)
        }
    }
}
