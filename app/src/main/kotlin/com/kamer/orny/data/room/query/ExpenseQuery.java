package com.kamer.orny.data.room.query;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.kamer.orny.data.room.entity.ExpenseEntity;
import com.kamer.orny.data.room.entity.ExpenseEntryEntity;

import java.util.List;

public class ExpenseQuery {

    @Embedded private ExpenseEntity expense;
    @Relation(parentColumn = "id", entityColumn = "expense_id")
    private List<ExpenseEntryEntity> entries;

    public ExpenseEntity getExpense() {
        return expense;
    }

    public void setExpense(ExpenseEntity expense) {
        this.expense = expense;
    }

    public List<ExpenseEntryEntity> getEntries() {
        return entries;
    }

    public void setEntries(List<ExpenseEntryEntity> entries) {
        this.entries = entries;
    }
}
