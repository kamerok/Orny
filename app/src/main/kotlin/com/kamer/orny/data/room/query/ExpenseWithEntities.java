package com.kamer.orny.data.room.query;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import com.kamer.orny.data.room.entity.ExpenseEntity;
import com.kamer.orny.data.room.entity.ExpenseEntryEntity;

import java.util.List;

public class ExpenseWithEntities {

    @Embedded private ExpenseEntity expense;
    @Relation(parentColumn = "id", entityColumn = "expense_id")
    private List<ExpenseEntryEntity> entries;

    public ExpenseWithEntities() {
    }

    public ExpenseWithEntities(@NonNull ExpenseEntity expense, @NonNull List<ExpenseEntryEntity> entries) {
        this.expense = expense;
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenseWithEntities)) return false;

        ExpenseWithEntities that = (ExpenseWithEntities) o;

        if (!expense.equals(that.expense)) return false;
        return entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        int result = expense.hashCode();
        result = 31 * result + entries.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return expense.toString() + " " + entries.toString();
    }

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
