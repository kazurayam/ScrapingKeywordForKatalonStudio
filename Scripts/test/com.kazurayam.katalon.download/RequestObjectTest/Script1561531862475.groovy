import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

TestObject to = findTestObject('Object Repository/New Request')
assert to != null

WebUI.comment("to.getActiveProperties()=${to.getActiveProperties()}")
WebUI.comment("to.getImagePath()=${to.getImagePath()}")
WebUI.comment("to.getObjectId()=${to.getObjectId()}")
WebUI.comment("to.getParentObject()=${to.getParentObject()}")
WebUI.comment("to.getProperties()=${to.getProperties()}")
WebUI.comment("to.getUseRelativeImagePath()=${to.getUseRelativeImagePath()}")
WebUI.comment("to.isParentObjectShadowRoot()=${to.isParentObjectShadowRoot()}")

RequestObject ro = (RequestObject)to
WebUI.comment("ro.getHttpBody()=${ro.getHttpBody()}")
WebUI.comment("ro.getHttpHeaderProperties()=${ro.getHttpHeaderProperties()}")
WebUI.comment("ro.getName()=${ro.getName()}")
WebUI.comment("ro.getObjectId()=${ro.getObjectId()}")
WebUI.comment("ro.getRestParameters()=${ro.getRestParameters()}")
WebUI.comment("ro.getRestRequestMethod()=${ro.getRestRequestMethod()}")
WebUI.comment("ro.getRestUrl()=${ro.getRestUrl()}")
WebUI.comment("ro.getServiceType()=${ro.getServiceType()}")
WebUI.comment("ro.getSoapBody()=${ro.getSoapBody()}")
WebUI.comment("ro.getSoapRequestMethod()=${ro.getSoapRequestMethod()}")
WebUI.comment("ro.getSoapServiceFunction()=${ro.getSoapServiceFunction()}")
WebUI.comment("ro.getWsdlAddress()=${ro.getWsdlAddress()}")
