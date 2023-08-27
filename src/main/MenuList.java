package main;

public class MenuList {
	private String menuId;
	private String menuName;
	private int menuPrice;
	private String menuType;
	
	public MenuList(String menuId, String menuName, int menuPrice, String menuType) {
		super();
		this.menuId = menuId;
		this.menuName = menuName;
		this.menuPrice = menuPrice;
		this.menuType = menuType;
	}
	
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public int getMenuPrice() {
		return menuPrice;
	}
	public void setMenuPrice(int menuPrice) {
		this.menuPrice = menuPrice;
	}
	public String getMenuType() {
		return menuType;
	}
	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}
	
}
