package org.techtown.project;

import android.view.View;

public interface OnPlanItemClickListener {
    public void onItemClick(PlanListAdapter.ViewHolder holder, View view, int position);
}
