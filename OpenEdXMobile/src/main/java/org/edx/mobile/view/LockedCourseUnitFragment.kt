package org.edx.mobile.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.greenrobot.event.EventBus
import org.edx.mobile.R
import org.edx.mobile.databinding.FragmentLockedCourseUnitBinding
import org.edx.mobile.event.CourseUpgradeDataReceivedEvent
import org.edx.mobile.model.api.CourseUpgradeResponse
import org.edx.mobile.model.api.EnrolledCoursesResponse

class LockedCourseUnitFragment : CourseUnitFragment() {
    companion object {
        @JvmStatic
        fun newInstance(courseData: EnrolledCoursesResponse,
                        courseUpgradeData: CourseUpgradeResponse?): LockedCourseUnitFragment {
            val fragment = LockedCourseUnitFragment()
            val bundle = Bundle()
            bundle.putSerializable(Router.EXTRA_COURSE_DATA, courseData)
            courseUpgradeData?.let {
                bundle.putSerializable(Router.EXTRA_COURSE_UPGRADE_DATA, courseUpgradeData)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentLockedCourseUnitBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_locked_course_unit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val courseUpgradeData = arguments?.getSerializable(Router.EXTRA_COURSE_UPGRADE_DATA)
        if (courseUpgradeData != null) {
            val courseData = arguments?.getSerializable(Router.EXTRA_COURSE_DATA) as EnrolledCoursesResponse
            loadPaymentBannerFragment(courseData, courseUpgradeData as CourseUpgradeResponse)
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this)
        }
    }

    fun onEvent(event: CourseUpgradeDataReceivedEvent) {
        val courseData = arguments?.getSerializable(Router.EXTRA_COURSE_DATA) as EnrolledCoursesResponse
        if (event.courseId == courseData.course.id) {
            loadPaymentBannerFragment(courseData, event.courseUpgradeData)
        }
    }

    private fun loadPaymentBannerFragment(courseData: EnrolledCoursesResponse, courseUpgradeData: CourseUpgradeResponse) {
        PaymentsBannerFragment.loadPaymentsBannerFragment(R.id.fragment_container, courseData,
                courseUpgradeData, false, childFragmentManager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}
