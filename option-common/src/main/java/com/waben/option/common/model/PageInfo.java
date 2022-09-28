package com.waben.option.common.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PageInfo<T> {

	/**
	 * 查询数据列表
	 */
	private List<T> records = Collections.emptyList();
	/**
	 * 总数
	 */
	private long total;
	/**
	 * 每页显示条数
	 */
	private int size;
	/**
	 * 当前页
	 */
	private int page;

	public PageInfo(List<T> records, long total, int page, int size) {
		super();
		this.records = records;
		this.total = total;
		this.size = size;
		this.page = page;
	}

	public PageInfo() {
		super();
	}
}
