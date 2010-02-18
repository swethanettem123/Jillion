/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;


public abstract class AbstractDefaultAceTag implements AceTag{
    private final String type;
    private final String creator;
    private final Date creationDate;
    private final String data;
    
    /**
     * @param id
     * @param type
     * @param creator
     * @param creationDate
     * @param location
     * @param data
     */
    public AbstractDefaultAceTag(String type, String creator,
            Date creationDate, String data) {
        this.type = type;
        this.creator = creator;
        this.creationDate = creationDate;
        this.data = data;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((creationDate == null) ? 0 : creationDate.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractDefaultAceTag)) {
            return false;
        }
        AbstractDefaultAceTag other = (AbstractDefaultAceTag) obj;
        if (creationDate == null) {
            if (other.creationDate != null) {
                return false;
            }
        } else if (!creationDate.equals(other.creationDate)) {
            return false;
        }
        if (creator == null) {
            if (other.creator != null) {
                return false;
            }
        } else if (!creator.equals(other.creator)) {
            return false;
        }
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
