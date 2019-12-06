package sunmi.common.model;

import sunmi.common.view.DropdownMenuNew;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class FilterItem extends DropdownMenuNew.Model {
    private int id;
    private String titleName;
    private String itemName;

    public FilterItem(int id, String name) {
        this.id = id;
        this.titleName = name;
        this.itemName = name;
    }

    public FilterItem(int id, String titleName, String itemName) {
        this.id = id;
        this.titleName = titleName;
        this.itemName = itemName;
    }

    public FilterItem(int id, String titleName, String itemName, boolean isChecked) {
        super(isChecked);
        this.id = id;
        this.titleName = titleName;
        this.itemName = itemName;
    }

    public int getId() {
        return id;
    }

    public String getTitleName() {
        return titleName;
    }

    public String getItemName() {
        return itemName;
    }

}
