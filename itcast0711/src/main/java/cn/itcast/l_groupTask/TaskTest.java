package cn.itcast.l_groupTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class TaskTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**部署流程定义（从inputStream）*/
	@Test
	public void deploymentProcessDefinition_zip(){
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("taskGroup.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("taskGroup.png");
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
							.createDeployment()//创建一个部署对象
							.name("组任务")//添加部署的名称
							.addInputStream("taskGroup.bpmn", inputStreamBpmn)
							.addInputStream("taskGroup.png", inputStreamPng)
							.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());//
		System.out.println("部署名称："+deployment.getName());//
	}
	
	/**启动流程实例*/
	@Test
	public void startProcessInstance(){
		//流程定义的key
		String processDefinitionKey = "taskGroup";
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
						.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID:"+pi.getId());//流程实例ID
		System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID 
	}
	
	/**查询当前人的组任务*/
	@Test
	public void findMyGroupTask(){
		String assignee = "小A";
		List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
						.createTaskQuery()//创建任务查询对象
						/**查询条件（where部分）*/
						.taskCandidateUser(assignee)//组任务的办理人查询
						/**排序*/
						.orderByTaskCreateTime().asc()//使用创建时间的升序排列
						/**返回结果集*/
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
		String taskId = "6004";
		//完成任务的同时，设置流程变量，使用流程变量来指定完成任务后，下一个连线。对应sequenceFlow.bpmn  ${meassage=="不重要"}
		processEngine.getTaskService()//与正在执行的任务管理相关的Service
					.complete(taskId);
		System.out.println("完成任务：任务ID："+taskId);
	}
	
	/**查询正在执行的任务办理人表*/
	@Test
	public void findRunPersonTask() {
		//任务ID
		String taskId = "6204";
		List<IdentityLink> list = processEngine.getTaskService()
									.getIdentityLinksForTask(taskId);
		if(list!=null && list.size()>0) {
			for(IdentityLink identityLink : list) {
				System.out.println(identityLink.getTaskId()+"   "+identityLink.getType()+"   "+identityLink.getUserId()+"   "+identityLink.getTaskId()+"   "+identityLink.getProcessInstanceId());
			}
		}
	}
	
	/**查询历史任务的办理人表*/
	@Test
	public void findHistoryPersonTask() {
		//流程实例ID
		String processInstanceId ="6201";
		List<HistoricIdentityLink> list = processEngine.getHistoryService()
					.getHistoricIdentityLinksForProcessInstance(processInstanceId);
		if(list!=null && list.size()>0) {
			for(HistoricIdentityLink identityLink : list) {
				System.out.println(identityLink.getTaskId()+"   "+identityLink.getType()+"   "+identityLink.getUserId()+"   "+identityLink.getTaskId()+"   "+identityLink.getProcessInstanceId());
			}
		}
	}
	
	/**拾取任务，将组任务分给个人任务，指定任务的办理人字段*/
	@Test
	public void claim() {
		//将组任务分配给个人
		//任务ID
		String taskId = "6204";
		String assignee = "大F";
		//分配的个人任务（可以时组任务中的成员，也可以是非组任务的成员）
		processEngine.getTaskService()
					.claim(taskId, assignee);
	}
	/**将个人任务回退到组任务，前提，之前一定是个组任务*/
	@Test
	public void setAssignee() {
		//任务ID
		String taskId = "6204";
		processEngine.getTaskService()
					.setAssignee(taskId, null);
	}
	
	/**流程定义的删除*/
	@Test
	public void deleteDefinitionTest() {
		String deploymentId = "1701";
		processEngine.getRepositoryService()
					.deleteDeployment(deploymentId, true);//带级联删除
		System.out.println("删除成功");
		
	}
}
