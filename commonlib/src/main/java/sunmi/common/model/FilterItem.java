package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import sunmi.common.view.DropdownMenuNew;

/**
 * @author yinhui
 * @since 2019-06-27
 */
public class FilterItem extends DropdownMenuNew.Model implements Parcelable {
    private int id;
    private String titleName;
    private String itemName;

    public FilterItem(int id, String name) {
        this.id = id;
        this.titleName = name;
        this.itemName = name;
    }

    public FilterItem(int id, String name, boolean isChecked) {
        super(isChecked);
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

    protected FilterItem(Parcel in) {
        id = in.readInt();
        titleName = in.readString();
        itemName = in.readString();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(titleName);
        dest.writeString(itemName);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FilterItem> CREATOR = new Creator<FilterItem>() {
        @Override
        public FilterItem createFromParcel(Parcel in) {
            return new FilterItem(in);
        }

        @Override
        public FilterItem[] newArray(int size) {
            return new FilterItem[size];
        }
    };

}
