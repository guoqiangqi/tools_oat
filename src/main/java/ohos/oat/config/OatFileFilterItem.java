package ohos.oat.config;

import java.util.Objects;

public class OatFileFilterItem {
    private String type;
    private String name;
    private String desc;
    private String ref;


    public OatFileFilterItem(String type, String name, String desc, String ref) {
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.ref = ref;
    }


    public String getName() {
        return this.name;
    }
    public String getRef() {
        return this.ref;
    }
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final OatFileFilterItem oatFileFilterItem = (OatFileFilterItem) o;
        return   this.name.equals(oatFileFilterItem.name) && this.type.equals(oatFileFilterItem.type)
                && this.desc.equals(oatFileFilterItem.desc) && this.ref.equals(oatFileFilterItem.ref) ;


    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.name, this.desc,this.ref);
    }
}
