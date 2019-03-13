package com.apexsoftware.quotable.managers.listeners;

import androidx.fragment.app.DialogFragment;

public interface OnDialogClickListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
    public void onDialogNeutralClick(DialogFragment dialog);
}
