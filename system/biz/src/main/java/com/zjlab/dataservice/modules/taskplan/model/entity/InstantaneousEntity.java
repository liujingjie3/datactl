package com.zjlab.dataservice.modules.taskplan.model.entity;

import lombok.Data;

import java.util.List;
@Data
public class InstantaneousEntity {

    private String originator; // 来源
    private String recipient;  // 目的地
    private List<Element> elements;  // 瞬时根数列表

    // Getters and Setters

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    // 内部类 Element，用于表示瞬时根数
    @Data
    public static class Element {
        private String satellite; // 卫星 ID
        private String epoch;     // 历元时间 (UTC 时)
        private double a;         // 轨道半长径 (单位：米)
        private double e;         // 轨道偏心率
        private double i;         // 轨道倾角 (单位：度)
        private double O;         // 升交点赤经 (单位：度)
        private double w;         // 近地点幅角 (单位：度)
        private double M;         // 平近点角 (单位：度)
        private Double Cd;        // 大气阻尼系数 (可选)
        private Double Cr;        // 光压反射系数 (可选)

        // Getters and Setters

        public String getSatellite() {
            return satellite;
        }

        public void setSatellite(String satellite) {
            this.satellite = satellite;
        }

        public String getEpoch() {
            return epoch;
        }

        public void setEpoch(String epoch) {
            this.epoch = epoch;
        }

        public double getA() {
            return a;
        }

        public void setA(double a) {
            this.a = a;
        }

        public double getE() {
            return e;
        }

        public void setE(double e) {
            this.e = e;
        }

        public double getI() {
            return i;
        }

        public void setI(double i) {
            this.i = i;
        }

        public double getO() {
            return O;
        }

        public void setO(double o) {
            O = o;
        }

        public double getW() {
            return w;
        }

        public void setW(double w) {
            this.w = w;
        }

        public double getM() {
            return M;
        }

        public void setM(double m) {
            M = m;
        }

        public Double getCd() {
            return Cd;
        }

        public void setCd(Double cd) {
            Cd = cd;
        }

        public Double getCr() {
            return Cr;
        }

        public void setCr(Double cr) {
            Cr = cr;
        }
    }
}
