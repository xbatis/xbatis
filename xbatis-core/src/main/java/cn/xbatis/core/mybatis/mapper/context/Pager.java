/*
 *  Copyright (c) 2024-2025, Ai东 (abc-127@live.cn) xbatis.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License").
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 */

package cn.xbatis.core.mybatis.mapper.context;

import cn.xbatis.page.IPager;
import cn.xbatis.page.PageUtil;
import cn.xbatis.page.PagerField;

import java.util.List;

public class Pager<T> implements IPager<T> {

    private Boolean executeCount = true;

    private List<T> results;

    private Integer total;

    private Integer number = 1;

    private Integer size = 20;

    public Pager() {

    }

    public Pager(int size) {
        this(1, size);
    }

    public Pager(int number, int size) {
        this.number = number;
        this.size = size;
    }

    public static <T> Pager<T> of(int size) {
        return new Pager<>(size);
    }

    public static <T> Pager<T> of(int number, int size) {
        return new Pager<>(number, size);
    }

    public Integer getOffset() {
        return PageUtil.getOffset(this.number, this.size);
    }

    public Boolean isExecuteCount() {
        return executeCount;
    }

    public Pager<T> setExecuteCount(boolean executeCount) {
        this.executeCount = executeCount;
        return this;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getTotalPage() {
        return PageUtil.getTotalPage(this.size, this.total);
    }

    @Override
    public <V> void set(PagerField<V> field, V value) {
        PagerGetSetUtil.set(this, field, value);
    }

    @Override
    public <V> V get(PagerField<V> field) {
        return PagerGetSetUtil.get(this, field);
    }

    @Override
    public String toString() {
        return "Pager{" +
                "executeCount=" + executeCount +
                ", results=" + results +
                ", total=" + total +
                ", number=" + number +
                ", size=" + size +
                '}';
    }
}
