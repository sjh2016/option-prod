package com.waben.option.data.entity.resource;

import java.util.HashMap;

public class DataRow extends HashMap {

    public DataRow() {
    }

    public void set(String name, String value) {
        if(name != null && !name.equals("")) {
            if(value == null) {
                this.put(name, "");
            } else {
                this.put(name, value);
            }

        }
    }

    public void set(String name, int value) {
        this.put(name, new Integer(value));
    }

    public void set(String name, boolean value) {
        this.put(name, new Boolean(value));
    }

    public void set(String name, long value) {
        this.put(name, new Long(value));
    }

    public void set(String name, float value) {
        this.put(name, new Float(value));
    }

    public void set(String name, double value) {
        this.put(name, new Double(value));
    }

    public void set(String name, Object value) {
        this.put(name, value);
    }

    public String getString(String name) {
        if(name != null && !name.equals("")) {
            Object obj = this.get(name);
            return obj == null?"":obj.toString();
        } else {
            return "";
        }
    }

    public int getInt(String name) {
        if(name != null && !name.equals("")) {
            boolean value = false;
            if(!this.containsKey(name)) {
                return 0;
            } else {
                Object obj = this.get(name);
                if(obj == null) {
                    return 0;
                } else {
                    int value1;
                    if(!(obj instanceof Integer)) {
                        try {
                            value1 = Integer.parseInt(obj.toString());
                        } catch (Exception var5) {
                            value1 = 0;
                        }
                    } else {
                        value1 = ((Integer)obj).intValue();
                        obj = null;
                    }

                    return value1;
                }
            }
        } else {
            return 0;
        }
    }

    public long getLong(String name) {
        if(name != null && !name.equals("")) {
            long value = 0L;
            if(!this.containsKey(name)) {
                return 0L;
            } else {
                Object obj = this.get(name);
                if(obj == null) {
                    return 0L;
                } else {
                    if(!(obj instanceof Long)) {
                        try {
                            value = Long.parseLong(obj.toString());
                        } catch (Exception var6) {
                            value = 0L;
                        }
                    } else {
                        value = ((Long)obj).longValue();
                        obj = null;
                    }

                    return value;
                }
            }
        } else {
            return 0L;
        }
    }

    public float getFloat(String name) {
        if(name != null && !name.equals("")) {
            float value = 0.0F;
            if(!this.containsKey(name)) {
                return 0.0F;
            } else {
                Object obj = this.get(name);
                if(obj == null) {
                    return 0.0F;
                } else {
                    if(!(obj instanceof Float)) {
                        try {
                            value = Float.parseFloat(obj.toString());
                        } catch (Exception var5) {
                            value = 0.0F;
                        }
                    } else {
                        value = ((Float)obj).floatValue();
                        obj = null;
                    }

                    return value;
                }
            }
        } else {
            return 0.0F;
        }
    }

    public double getDouble(String name) {
        if(name != null && !name.equals("")) {
            double value = 0.0D;
            if(!this.containsKey(name)) {
                return 0.0D;
            } else {
                Object obj = this.get(name);
                if(obj == null) {
                    return 0.0D;
                } else {
                    if(!(obj instanceof Double)) {
                        try {
                            value = Double.parseDouble(obj.toString());
                        } catch (Exception var6) {
                            value = 0.0D;
                        }
                    } else {
                        value = ((Double)obj).doubleValue();
                        obj = null;
                    }

                    return value;
                }
            }
        } else {
            return 0.0D;
        }
    }

    public boolean getBoolean(String name) {
        if(name != null && !name.equals("")) {
            boolean value = false;
            if(!this.containsKey(name)) {
                return false;
            } else {
                Object obj = this.get(name);
                if(obj == null) {
                    return false;
                } else if(obj instanceof Boolean) {
                    return ((Boolean)obj).booleanValue();
                } else {
                    value = Boolean.valueOf(obj.toString()).booleanValue();
                    obj = null;
                    return value;
                }
            }
        } else {
            return false;
        }
    }

    public Object getObject(String name) {
        return name != null && !name.equals("")?(!this.containsKey(name)?null:this.get(name)):null;
    }

}
