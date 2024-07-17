package six.yiting.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import six.yiting.model.StoresBean;

@NoArgsConstructor
@Setter
@Getter
public class SelectProductDto {
	
	private List<StoresBean> stores;
	private List<String> selectedItems;

}
