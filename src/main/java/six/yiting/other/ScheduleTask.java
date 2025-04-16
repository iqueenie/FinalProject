package six.yiting.other;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import six.yiting.model.BuyBean;
import six.yiting.service.BuyService;
import six.yiting.service.DetailService;
import six.yiting.service.InventoryService;
import six.yiting.service.StoreService;

@Component
public class ScheduleTask {
	
	@Autowired
	BuyService buyService;
	
	@Autowired
	DetailService detailService;
	
	@Autowired InventoryService invService;
	
	@Autowired StoreService storeService;

	//@Scheduled(cron="*/5 * * * * ?")
	//	@Scheduled(initialDelay = 2000)
	@Scheduled(cron="0 31 15 * * ?")
	public List<Integer> buyAutoInsert() {
		List<BuyBean> notCheckList = buyService.findByArriveDateAndCheckToInv();
		List<Integer> resultList = new ArrayList<>();
		if(notCheckList != null) {
			for(BuyBean buy:notCheckList) {
				buyService.buyInsertInv(buy.getPurchaseId());
				resultList.add(buy.getPurchaseId());
			}
			return resultList;
		}
		return null;
	}
	
	
}
