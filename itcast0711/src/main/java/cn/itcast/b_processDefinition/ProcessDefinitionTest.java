package cn.itcast.b_processDefinition;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

public class ProcessDefinitionTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/** 部署流程定义（从classptah） */
	@Test
	public void deploymentProcessDefinition_classPath() {
		
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的service
					.createDeployment()//创建一个部署对象
					.name("流程定义")//添加部署的名称
					.addClasspathResource("diagrams/helloworld.bpmn")//从classpath的资源中加载，一次只能加载一个文件
					.addClasspathResource("diagrams/helloworld.png")//从classptah的资源文件中加载，一次只能加载一个文件
					.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
	}
	/** 部署流程定义（从zip中） */
	@Test
	public void deploymentProcessDefinition_zip() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的service
				.createDeployment()//创建一个部署对象
				.name("流程定义")//添加部署的名称
				.addZipInputStream(zipInputStream)//指定zip格式的文件完成部署
				.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());
		System.out.println("部署名称："+deployment.getName());
		
	}
}
