package com.zjs.bwcx.bigdata.udf;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

@Description(name = "deprecation", value = "_FUNC_(date, price) - from the input date string(yyyyMMdd), "
        + "returns the deprecation price by computing price and "
        + "the depreciation rate of the second-hand car.", extended = "Example:\n"
                + " > SELECT _FUNC_(date_string, price) FROM src;")
public class TestUDF extends UDF {

    private SimpleDateFormat df;

    private double[] rates;

    public TestUDF() {
        df = new SimpleDateFormat("yyyyMMdd");
        rates = new double[] {0.071, 0.070, 0.069, 0.067, 0.064, 0.062, 0.060, 0.057, 0.055, 0.053, 0.051, 0.049, 0.049,
                0.048, 0.046, 0.044 };
    }

    @SuppressWarnings("deprecation")
    public String evaluate(String deal_date, String price) {
        Date date = null;
        Date now = new Date();// 获取当前时间
        long price_ = Long.valueOf(price);
        try {
            date = df.parse(deal_date);
            /* 37-45行是固定的算法，可以替换自己的算法 */
            double numMonth = (now.getYear() * 12 + now.getMonth()) - (date.getYear() * 12 + now.getMonth()) + 1;
            double mileage = ((6.000 - (1.7000 * numMonth / 12)) / 1.7000) * 12 * 0.25;
            double total = numMonth + mileage;
            int depre_year = (int) (total / 12);
            for (int i = 0; i < depre_year + 1; i++) {
                price_ = (long) (price_ * (1 - rates[i]));
            }
        } catch (Exception ex) {
            return null;
        }
        return Long.toString(price_);
    }

}
