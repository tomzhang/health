package com.dachen.health.controller.system;

import com.dachen.commons.JSONMessage;
import com.dachen.health.disease.dao.DiseaseDepartmentRepository;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseDepartment;
import com.dachen.health.disease.entity.DiseaseType;
import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;

@RestController
@RequestMapping("/datainit")
public class DataInitController {

	private static Logger logger = LoggerFactory.getLogger(DataInitController.class);

	@Resource
	private DiseaseDepartmentRepository diseaseDepratmentRepository;

	@Resource
	private DiseaseTypeRepository diseaseTypeRepository;

	@RequestMapping("importDiseaseType")
	public JSONMessage importDiseaseType() {
		URL url = getClass().getProtectionDomain().getClassLoader()
				.getResource("/");

		if (url != null) {
			String filePath = url.getPath() + "initdata/diseaseType.txt";
			File f = new File(filePath);
			if (!f.exists()) {
				logger.info(f.getAbsolutePath() + " is not exsist");
			}
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						filePath));
				explain(reader);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(
										"X:/health1/health-im-api/src/main/resources/initdata/diseaseType.txt"),
								"utf-8"));

				explain(reader);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return JSONMessage.success();
	}

	public String explain(BufferedReader reader) throws IOException {
		StringBuffer sb = new StringBuffer();

		while (reader.ready()) {
			sb.append(reader.readLine());
			sb.append("\n");
		}

		String x[] = sb.toString().split("\n\n");
		for (String xy : x) {
			String[] n = xy.split(" ");
			DiseaseDepartment dept = new DiseaseDepartment();
			dept.setName(n[0]);
			Key<DiseaseDepartment> d = diseaseDepratmentRepository.save(dept);

			String nxd[] = n[1].split("\\|");
			for (String sk : nxd) {
				DiseaseType type = new DiseaseType();
				type.setDepartment(d.getId().toString());
				type.setName(removeNewLine(sk));
				diseaseTypeRepository.save(type);
			}

		}
		return sb.toString();
	}

	private String removeNewLine(String s) {
		if (s != null) {
			s = s.replace("\n", "");
		}
		return s;
	}

	public static void main(String[] args) {

		DataInitController c = new DataInitController();
		System.out.println(c.importDiseaseType());

	}

}
