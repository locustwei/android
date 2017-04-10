/**
 * TreeView适配器
 */
package com.wisecleaner.views;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TreeViewAdapter extends BaseAdapter{

	private static final int PaddingLeft = 38;
	
	private TreeViewAdapterLinstener mLinstener;
	private TreeListView treeView;
	private TreeNode mNode;
	
	public TreeViewAdapter(TreeViewAdapterLinstener linstener, TreeListView treeView) {
		mLinstener = linstener;
		mNode = new TreeNode();
		this.treeView = treeView;
		treeView.setAdapter(this);
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	public void buildTree() {
		mNode.buildNode(0);
		mNode.expanded = true;
		notifyDataSetChanged();
	}
	
	public void handleItemClick(View view, Object tag) {
		ViewHolder holder = (ViewHolder)view.getTag();
		setExpanded(holder.node, !holder.node.expanded);
	}

	public void setExpanded(TreeNode node, boolean expand) {
		if(node.getChildCount()==0)
			return;
		
		node.expanded = expand;
		notifyDataSetChanged();
	}
	
	public TreeNode getRootNode() {
		return mNode;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getVisibleCount(mNode);
	}

	@Override
	public Object getItem(int position) {
		TreeNode node = getVisibleNode(mNode, -1, position);
		if(node!=null)
			return node.object;
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		TreeNode node = getVisibleNode(mNode, -1, position);
		LinearLayout layout;
		if(convertView!=null){
			holder = (ViewHolder)convertView.getTag();
			layout = (LinearLayout)convertView;
			View view = mLinstener.getView(node.object, holder.userView, layout);
			if(holder.userView != view){
				layout.removeView(holder.userView);
				layout.addView(view);
				holder.userView = view;
			}
		}else { 
			holder = new ViewHolder();

			holder.itemView = new LinearLayout(mLinstener.getContext());
			holder.itemView.setBackgroundColor(0xFFFFFFFF);
			holder.itemView.setOrientation(LinearLayout.HORIZONTAL);
			holder.itemView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			
			holder.exView = new ImageView(mLinstener.getContext());
			holder.itemView.addView(holder.exView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			
			holder.userView = mLinstener.getView(node.object, null, holder.itemView);
			holder.itemView.addView(holder.userView);
			holder.itemView.setTag(holder);
		}
		
		holder.node = node;
		if(node.getChildCount()==0)
			holder.exView.setVisibility(View.GONE);
		else if (node.expanded && treeView.getExpendDrawable()!=null){
			holder.exView.setVisibility(View.VISIBLE);
			holder.exView.setImageDrawable(treeView.getExpendDrawable());
		}else if(!node.expanded && treeView.getCollapseDrawable()!=null){
			holder.exView.setVisibility(View.VISIBLE);
			holder.exView.setImageDrawable(treeView.getCollapseDrawable());
		}else
			holder.exView.setVisibility(View.GONE);
		
		holder.itemView.setPadding(15, 0, 0, 0);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			     LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(node.level * PaddingLeft, 0, 0, 0);
		holder.userView.setLayoutParams(params);
		
		return holder.itemView;
	}
	
	/**
	 * 回调接口，方便外部控制
	 * @author asa
	 *
	 */
	public interface TreeViewAdapterLinstener {
		public Context getContext();
		public View getView(Object data, View convertView, ViewGroup parent);
		public int getClildCount(Object data);
		public Object getGroupChild(Object group, int position);
	}

	private int getVisibleCount(TreeNode node){
		int result = 0;
		
		if(node.expanded && node.mChilds!=null){
			result = node.mChilds.size();
			for(int i=0; i<node.mChilds.size(); i++)
				result += getVisibleCount(node.mChilds.get(i));
		}
		return result;
	}

	private TreeNode getVisibleNode(TreeNode node, int cur, int position) {
		if(cur == position)
			return node;
		if(node.expanded && node.mChilds!=null){
			for(int i=0; i<node.mChilds.size(); i++){
				cur++;
				TreeNode child = getVisibleNode(node.mChilds.get(i), cur, position);
				if(child!=null)
					return child;
				cur += getVisibleCount(node.mChilds.get(i));
			}
		}
		return null;
		
	}

	/**
	 * Tree数据结构
	 * @author asa
	 *
	 */
	public class TreeNode{
		private Object object;
		private ArrayList<TreeNode> mChilds = new ArrayList<TreeViewAdapter.TreeNode>();
		private TreeNode parent;
		private boolean expanded = false;
		private int level;
		
		public TreeNode getNode(int position) {
			return mChilds.get(position);					
		}
		
		public int getChildCount() {
			return mChilds.size();
		}

		public TreeNode addNode(int position, Object data) {
			TreeNode node = new TreeNode();
			node.parent = this;
			node.object = data;
			node.level = level + 1;
			mChilds.add(position, node);
			return node;
		}
		
		public TreeNode getParent() {
			return parent;
		}
		
		public Object getObject() {
			return object;
		}
		
		public void setObject(Object object) {
			this.object = object;
		}
		
		public ArrayList<TreeNode> getChilds() {
			return mChilds;
		}
		
		
		private void buildNode(int level){
			mChilds.clear();
			int count = mLinstener.getClildCount(object);
			if(count>0){
				expanded = false;
				for(int i= 0; i<count; i++){
					TreeNode node = new TreeNode();
					node.object = mLinstener.getGroupChild(object, i);
					node.parent = this;
					node.level = level;
					node.buildNode(level + 1);
					mChilds.add(node);
				}
			}
		}
	}
	
	private class ViewHolder{
		TreeNode node;
		LinearLayout itemView;
		ImageView exView;
		View userView;
	}
	
}
