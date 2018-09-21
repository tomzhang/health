package com.dachen.health.circle.controller;

import com.dachen.health.circle.service.Group2Service;
import com.dachen.health.circle.service.GroupDoctor2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/circle/group/doctor")
public class CircleGroupDoctorController extends CircleBaseController {

    @Autowired
    protected Group2Service group2Service;

    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;


}
