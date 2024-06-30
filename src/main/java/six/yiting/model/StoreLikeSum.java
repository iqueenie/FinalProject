package six.yiting.model;

public class StoreLikeSum implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int storeId;
	private String storeName;
	private long followers;
	
	public StoreLikeSum(int storeId, String storeName, long followers) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.followers = followers;
	}
	
	public int getStoreId() {
		return storeId;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public long getFollowers() {
		return followers;
	}
	public void setFollowers(int followers) {
		this.followers = followers;
	}
	
	
	

	
}