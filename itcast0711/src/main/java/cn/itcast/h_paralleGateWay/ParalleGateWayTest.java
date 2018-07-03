package cn.itcast.h_paralleGateWay;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class ParalleGateWayTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**部署流程定义（从inputStream）*/
	@Test
	public void deploymentProcessDefinition_zip(){
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("paralleGateWay.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("paralleGateWay.png");
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
							.createDeployment()//创建一个部署对象
							.name("并行网关")//添加部署的名称
							.addInputStream("paralleGateWay.bpmn", inputStreamBpmn)
							.addInputStream("paralleGateWay.png", inputStreamPng)
							.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());//
		System.out.println("部署名称："+deployment.getName());//
	}
	
	/**启动流程实例*/
	@Test
	public void startProcessInstance(){
		//流程定义的key
		String processDefinitionKey = "paralleGateWay";
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
						.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:"+pi.getId());//流程实例ID    101
		System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   helloworld:1:4
	}
	
	/**查询当前人的个人任务*/
	@Test
	public void findMyPersonalTask(){
		String assignee = "赵小六";
		List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
						.createTaskQuery()//创建任务查询对象
						/**查询条件（where部分）*/
						.taskAssignee(assignee)//指定个人任务查询，指定办理人
//						.taskCandidateUser(candidateUser)//组任务的办理人查询
//						.processDefinitionId(processDefinitionId)//使用流程定义ID查询
//						.processInstanceId(processInstanceId)//使用流程实例ID查询
//						.executionId(executionId)//使用执行对象ID查询
						/**排序*/
						.orderByTaskCreateTime().asc()//使用创建时间的升序排列
						/**返回结果集*/
//						.singleResult()//返回惟一结果集
//						.count()//返回结果集的数量
//						.listPage(firstResult, maxResults);//分页查询
						.list();//返回列表
		if(list!=null && list.size()>0){
			for(Task task:list){
				System.out.println("任务ID:"+task.getId());
				System.out.println("任务名称:"+task.getName());
				System.out.println("任务的创建时间:"+task.getCreateTime());
				System.out.println("任务的办理人:"+task.getAssignee());
				System.out.println("流程实例ID："+task.getProcessInstanceId());
				System.out.println("执行对象ID:"+task.getExecutionId());
				System.out.println("流程定义ID:"+task.getProcessDefinitionId());
				System.out.println("########################################################");
			}
		}
	}
	
	/**完成我的任务*/
	@Test
	public void completeMyPersonalTask(){
		//任务ID
		String taskId = "4202";
		//完成任务的同时，设置流程变量，使用流程变量来指定完成任务后，下一个连线。对应sequenceFlow.bpmn  ${meassage=="不重要"}
		processEngine.getTaskService()//与正在执行的任务管理相关的Service
					.complete(taskId);
		System.out.println("完成任务：任务ID："+taskId);
	}
	
	/**查询流程状态（判断流程正在执行，还是结束）*/
	@Test
	public void isProcessEnd(){
		String processInstanceId = "1002";
		ProcessInstance pi = processEngine.getRuntimeService()//表示正在执行的流程实例和执行对象
						.createProcessInstanceQuery()//创建流程实例查询
						.processInstanceId(processInstanceId)//使用流程实例ID查询
						.singleResult();
		if(pi==null){
			System.out.println("流程已经结束");
		}
		else{
			System.out.println("流程没有结束");
		}
	}
	
	@Test
	public void isProcessEnd_2() {
		String processInstanceId = "101";
		HistoricProcessInstance hpi = processEngine.getHistoryService()
										.createHistoricProcessInstanceQuery()
										.processInstanceId(processInstanceId)
										.singleResult();
		if(hpi==null) {
			System.out.println("该流程实例不存在");
		}else if(hpi.getEndTime()!=null) {
			System.out.println("流程已结束");
		}else {
			System.out.println("流程没有结束");
		}
	}
	
	/**流程定义的删除*/
	@Test
	public void deleteDefinitionTest() {
		String deploymentId = "3701";
		processEngine.getRepositoryService()
					.deleteDeployment(deploymentId, true);//带级联删除
		System.out.println("删除成功");
	}
}