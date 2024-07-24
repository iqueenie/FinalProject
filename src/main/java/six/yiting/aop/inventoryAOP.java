package six.yiting.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	        HttpSession session = RequestContextHolder.currentRequestAttributes() instanceof ServletRequestAttributes
                    ? ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(false)
                    : null;
            ManagementDTO user = (session != null) ? (ManagementDTO) session.getAttribute("logInManagement") : null;
            logToFile(String.format(", user: %s",user.getManagementAccount()), false);
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
	        HttpSession session = RequestContextHolder.currentRequestAttributes() instanceof ServletRequestAttributes
                    ? ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(false)
                    : null;
            ManagementDTO user = (session != null) ? (ManagementDTO) session.getAttribute("logInManagement") : null;
            logToFile(String.format(", user: %s",user.getManagementAccount()), false);
	    	
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
	        HttpSession session = RequestContextHolder.currentRequestAttributes() instanceof ServletRequestAttributes
                    ? ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(false)
                    : null;
            ManagementDTO user = (session != null) ? (ManagementDTO) session.getAttribute("logInManagement") : null;
            logToFile(String.format(", user: %s",user.getManagementAccount()), false);
            
            Integer invId = (Integer)args[0];
            InventoryBean inv = invService.findInvById(invId);
            logToFile(String.format(", inventoryId: %d",inv.getInventoryId()), true);
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