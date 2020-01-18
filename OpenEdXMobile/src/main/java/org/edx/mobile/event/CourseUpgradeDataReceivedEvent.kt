package org.edx.mobile.event

import org.edx.mobile.model.api.CourseUpgradeResponse

class CourseUpgradeDataReceivedEvent(val courseId: String, val courseUpgradeData: CourseUpgradeResponse) : BaseEvent()
