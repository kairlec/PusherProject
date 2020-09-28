package com.kairlec.pusher.controller

import com.kairlec.intf.ResponseDataInterface
import com.kairlec.pusher.annotation.ResponseResult
import com.kairlec.pusher.core.wework.WeWorkAddressBookHelper
import com.kairlec.utils.ResponseDataUtil.responseOK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*

/**
 * 通讯录Controller
 * 部门和用户相关API
 */
@RestController
@RequestMapping(value = ["/addressbook"], produces = ["application/json"])
@ConditionalOnProperty(prefix = "wework.addressbook", value = ["enabled"], matchIfMissing = true)
@ResponseResult
class AddressBookController {

    @Autowired
    private lateinit var workAddressBookHelper: WeWorkAddressBookHelper

    /**
     * 获取部门信息
     * @param id 部门id,非必须,默认为0
     */
    @RequestMapping(value = ["/department/{id}", "/department"], method = [RequestMethod.GET])
    fun getUserSimpleList(@PathVariable(required = false) id: Int?): ResponseDataInterface {
        return workAddressBookHelper.getDepartmentList(id).responseOK
    }

    /**
     * 获取部门下简单用户信息
     * @param id 部门id
     * @param fetchChild 是否递归获取子部门用户
     */
    @RequestMapping(value = ["/department/{id}/user"], method = [RequestMethod.GET])
    fun getUserSimpleList(@PathVariable id: Int,
                          @RequestParam(value = "fetchChild", required = false, defaultValue = "true") fetchChild: Boolean): ResponseDataInterface {
        return workAddressBookHelper.getUserSimpleList(id, fetchChild).responseOK
    }
}