package six.yiting.other;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

import six.hsiao.dto.ManagementDTO;
import six.yiting.model.InventoryBean;
import six.yiting.service.InventoryService;

@Aspect
@Component
public class inventoryAOP {

	    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	    private static final String LOG_FILE_PATH = "logs/app.log";
	    
	    @Autowired
	    InventoryService invService;

	    @Before("execution(* six.yiting.service.InventoryService.saveInventory(..))")
	    public void logBeforeExecution() {
	        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	        String startTimeStr = dateFormat.format(new Date());
	        
	        logToFile(String.format("Method insert start time: %s", startTimeStr), false);
	        
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	        if (attributes != null) {
	            HttpSession session = attributes.getRequest().getSession(false);
	            ManagementDTO user = (session != null) ? (ManagementDTO) session.getAttribute("logInManagement") : null;
	            logToFile(String.format(", user: %s",user.getManagementAccount()), false);
	        }
	    }
	    
	    @AfterReturning(pointcut = "execution(* six.yiting.service.InventoryService.saveInventory(..))", returning = "result")
	    public void logAfterReturning(Object result) {
	        if (result instanceof InventoryBean) {
	            InventoryBean inventoryBean = (InventoryBean) result;
	            logToFile(String.format(", inventoryId: %d",inventoryBean.getInventoryId()), true);
	        }
	    }
	    
	    
	    @Before("execution(* six.yiting.controller.InventoryController.editPost(..))")
	    public void logBeforeEditPost(JoinPoint joinPoint) {
	    	
	    	
	    	Object[] args = joinPoint.getArgs();
	    	
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	        String startTimeStr = dateFormat.format(new Date());
	        
	        logToFile(String.format("Method update start time: %s", startTimeStr), false);
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	        if (attributes != null) {
	            HttpSession session = attributes.getRequest().getSession(false);
	            ManagementDTO user = (session != null) ? (ManagementDTO) session.getAttribute("logInManagement") : null;
	            logToFile(String.format(", user: %s",user.getManagementAccount()), false);
	        }
            Integer invId = (Integer)args[4];
            InventoryBean inv = invService.findInvById(invId);
            logToFile(String.format(", inventoryId: %d",inv.getInventoryId()), false);
            logToFile(String.format(",original delivery date: %s", inv.getDeliveryDate()), false);
	    	logToFile(String.format(",original number: %s", inv.getInvNum()), false);
	        
	    }
	    
	    @AfterReturning(pointcut = "execution(* six.yiting.service.InventoryService.updateInv(..))", returning = "result")
	    public void logAfterReturningUpdate(Object result) {
	    	
	        if (result instanceof InventoryBean) {
	            InventoryBean inv = (InventoryBean) result;
	            logToFile(String.format(",new delivery date: %s", inv.getDeliveryDate()), false);
		    	logToFile(String.format(",new number: %s", inv.getInvNum()), true);
	        }
	    }
	    
	    
	    @Before("execution(* six.yiting.service.InventoryService.deleteInventory(..))")
	    public void logBeforeDelete(JoinPoint joinPoint) {
	    	
	    	Object[] args = joinPoint.getArgs();
	    	
	        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	        String startTimeStr = dateFormat.format(new Date());
	        
	        logToFile(String.format("Method delete start time: %s", startTimeStr), false);
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	        if (attributes != null) {
	            HttpSession session = attributes.getRequest().getSession(false);
	            ManagementDTO user = (session != null) ? (ManagementDTO) session.getAttribute("logInManagement") : null;
	            logToFile(String.format(", user: %s",user.getManagementAccount()), false);
	        }
            Integer invId = (Integer)args[0];
            InventoryBean inv = invService.findInvById(invId);
            logToFile(String.format(", inventoryId: %d",inv.getInventoryId()), true);
	    }
	    
	    @AfterReturning(pointcut ="execution(* six.yiting.other.ScheduleTask.buyAutoInsert(..))", returning = "result")
	    public void buyAutoInsert(Object result) {
	    	
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	        String startTimeStr = dateFormat.format(new Date());
	        
	        logToFile(String.format("Auto insert Purchase to Inventory end time: %s purchaseId: ", startTimeStr), false);
	    	
	        int i =0;
	    	 if (result instanceof List<?>) {
	    		 
    	        List<?> resultList = (List<?>) result;

    	        // 确保列表中每个元素都是 Integer 类型
    	        for (Object item : resultList) {
    	        	i=i+1;
    	            if (item instanceof Integer && i == resultList.size()) {
    	            	Integer id = (Integer) item;
    	            	logToFile(String.format("%d",id), true);
    	            }else {
    	                Integer id = (Integer) item;
    	                logToFile(String.format("%d, ", id), false);
    	            }
    	        }
	    	 }
	        
	    }
	    
	    @After("execution(* six.yiting.controller.BuyController.buyCheck(..))")
	    public void buyInsertInInv(JoinPoint joinPoint) {
	    	
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	        String startTimeStr = dateFormat.format(new Date());

	        Object[] args = joinPoint.getArgs();
	        Integer buyId = (Integer)args[0];
	        
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	        if (attributes != null) {
	            HttpSession session = attributes.getRequest().getSession(false);
	            ManagementDTO user = (session != null) ? (ManagementDTO) session.getAttribute("logInManagement") : null;
	            logToFile(String.format("User: %s",user.getManagementAccount()), false);
	        }
	        
	        logToFile(String.format(", insert Purchase to Inventory end time: %s purchaseId: ", startTimeStr), false);
	    	
	        
    	    logToFile(String.format("%d",buyId), true);
    	            
	    	 
	        
	    }

	    private void logToFile(String message, boolean addNewLine) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
	            writer.write(message);
	            if (addNewLine) {
	                writer.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}