package com.github.su_takizawa.wordcard

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class ListBaseActivity<C, T, EC> : AppCompatActivity() {

    val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }
    lateinit var adapter: ListAdapter<T, RecyclerView.ViewHolder>
    lateinit var recyclerView: RecyclerView
    lateinit var swipeToDismissTouchHelper: DeleteItemTouchHelper

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                resultOkProcess(result)
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.not_saved,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    abstract fun resultOkProcess(result: ActivityResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivity())

        recyclerView = findViewById<RecyclerView>(getRecyclerView())
        initRecyclerView()
        swipeToDismissTouchHelper = DeleteItemTouchHelper(adapter)
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView)


        //?????????????????????
        val fab = findViewById<FloatingActionButton>(getFabAdd())
        fab.setOnClickListener {
            val intent = Intent(this@ListBaseActivity, getEditClass())
//            intent.putExtra("BODY",getAddRequest())
            intent.putExtra("MODE", EditBaseActivity.Mode.ADD.toString())
            intent.putExtra("BODY", getAddRequest())
            startForResult.launch(intent)
        }


        val fabEdit = findViewById<FloatingActionButton>(getFabEdit())
        fabEdit.isEnabled = false

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                var i = 0
                while (i < adapter.itemCount) {
                    val item =
                        recyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<RadioButton>(
                            getRecycleRb()
                        )
                    if (item?.isChecked == true) {
                        //?????????????????????????????????????????????????????????Edit???????????????????????????
                        fabEdit.isEnabled = true
                        return
                    }
                    i++
                }
                fabEdit.isEnabled = false
            }
        })

        fabEdit.setOnClickListener {
            var i = 0
            Log.v("TAG", "itemCount" + adapter.itemCount)
            while (i < adapter.itemCount) {
                val recycleItem =
                    recyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<RadioButton>(
                        getRecycleRb()
                    )
                if (recycleItem?.isChecked == true) {
                    Log.v("TAG", recycleItem.text.toString())

                    val item: T = adapter.currentList[i] as T
                    Log.v("TAG", "adapter.current:$item")

                    val intent = Intent(this@ListBaseActivity, getEditClass())
                    intent.putExtra("MODE", EditBaseActivity.Mode.EDIT.toString())
                    intent.putExtra("BODY", getEditRequest(item))
                    startForResult.launch(intent)
                }
                i++
            }

        }
    }

    inner class DeleteItemTouchHelper(private val adapter: ListAdapter<T, RecyclerView.ViewHolder>) :
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.RIGHT
        ) {
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val folder: T = adapter.currentList[viewHolder.adapterPosition] as T
                val deleteFlagment = DeleteConfirmDialogFlagment(
                    DialogButtonClickListener(folder)
                )
                deleteFlagment.show(supportFragmentManager, "DeleteConfirmDialogFlagment")
                adapter.notifyDataSetChanged()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.RED)
                val deleteIcon = AppCompatResources.getDrawable(
                    this@ListBaseActivity,
                    R.drawable.ic_baseline_delete_24
                )
                val iconMarginVartical =
                    (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2

                deleteIcon.setBounds(
                    itemView.left + iconMarginVartical,
                    itemView.top + iconMarginVartical,
                    itemView.left + iconMarginVartical + deleteIcon.intrinsicWidth,
                    itemView.bottom - iconMarginVartical
                )
                background.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.right + dX.toInt(),
                    itemView.bottom
                )
                background.draw(c)
                deleteIcon.draw(c)
            }
        }
        )

    inner class DialogButtonClickListener(val folder: T) : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Log.v("TAG", "DELETE!!")
                    delete(folder)
                }
            }
        }
    }

    class DeleteConfirmDialogFlagment(val dialogListener: DialogInterface.OnClickListener) :
        DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = context?.let { AlertDialog.Builder(it) }
            builder?.setMessage("????????????????????????????????????")
            builder?.setPositiveButton(R.string.dialog_btn_ok, dialogListener)
            return builder?.create() as Dialog
        }
    }

    /**
     * ???Activity????????????????????????
     * @return ???Activity?????????
     */
    protected abstract fun getActivity(): Int

    /**
     * ???RecyclerView????????????????????????
     * @return ???RecyclerView?????????
     */
    abstract fun getRecyclerView(): Int

    /**
     * RecyclerView??????????????????
     */
    abstract fun initRecyclerView()

    /**
     * ???RadioButton????????????????????????
     * @return ???RadioButton?????????
     */
    abstract fun getRecycleRb(): Int

    /**
     * ???TextView????????????????????????
     * @return ???TextView?????????
     */
    abstract fun getRecycleTv(): Int

    /**
     * ???FabAdd????????????????????????
     * @return ???FabAdd?????????
     */
    abstract fun getFabAdd(): Int

    /**
     * ???FabEdit????????????????????????
     * @return ???FabEdit?????????
     */
    abstract fun getFabEdit(): Int

    /**
     * ????????????
     */
    abstract fun delete(item: T)

    /**
     * ???????????????Class???????????????
     * @return ????????????Class
     */
    abstract fun getEditClass(): Class<EC>

    /**
     * ?????????????????????????????????????????????
     * @param item ????????????
     * @return ????????????????????????
     */
    abstract fun getEditRequest(item: T): String

    /**
     * ?????????????????????????????????????????????
     * @return ????????????????????????
     */
    abstract fun getAddRequest(): String
}

