package com.ganesha.accounting.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ganesha.model.LogableEntity;
import com.ganesha.model.Trackable;

@Entity
@Table(name = "EXPENSE_TRANSACTIONS")
public class ExpenseTransaction extends Trackable implements LogableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "EXPENSE_ID", nullable = false)
	private Expense expense;

	@Column(name = "AMOUNT", nullable = false)
	private BigDecimal amount;

	@Column(name = "NOTES", nullable = false)
	private String notes;

	public BigDecimal getAmount() {
		return amount;
	}

	public Expense getExpense() {
		return expense;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public String getNotes() {
		return notes;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
