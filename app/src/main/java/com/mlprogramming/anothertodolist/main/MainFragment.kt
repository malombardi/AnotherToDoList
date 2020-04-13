package com.mlprogramming.anothertodolist.main

import android.graphics.Canvas
import android.graphics.Color.parseColor
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.mlprogramming.anothertodolist.AnotherToDoListApplication
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.user.UserManager
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    lateinit var mainViewModel: MainViewModel
    lateinit var navigator: Navigator

    private var mFirebaseAdapter: FirebaseRecyclerAdapter<ToDoTask, TaskViewHolder>? = null
    private lateinit var manager: LinearLayoutManager

    private lateinit var userManager: UserManager
    private lateinit var storageManager: StorageManager
    private lateinit var deleteIcon: Drawable
    private var colorDrawableBackground = ColorDrawable(parseColor("#f7f7f7"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userManager =
            (activity!!.application as AnotherToDoListApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)

        storageManager =
            (activity!!.application as AnotherToDoListApplication).appComponent.storageManager()

        navigator = Navigator((activity as MainActivity).getNavController())
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.setUserManager(userManager)

        setupView()
        setupStateObserver()
    }

    private fun setupView() {
        deleteIcon =
            ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.ic_remove)!!
        manager = LinearLayoutManager(activity).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        tasksRecyclerView.apply {
            layoutManager = manager
            setHasFixedSize(true)
        }

        addTask.setOnClickListener {
            mainViewModel.onHandleIntent(UiIntent.AddTask)
        }
        enableSwipe()
    }

    private fun enableSwipe() {
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    viewHolder2: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                    mainViewModel.onHandleIntent(
                        UiIntent.DeleteTask(
                            mFirebaseAdapter!!.getItem(
                                viewHolder.adapterPosition
                            )
                        )
                    )
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
                    val itemView = viewHolder.itemView
                    val iconMarginVertical =
                        (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                    if (dX > 0) {
                        colorDrawableBackground.setBounds(
                            itemView.left,
                            itemView.top,
                            dX.toInt(),
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.left + iconMarginVertical,
                            itemView.top + iconMarginVertical,
                            itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                            itemView.bottom - iconMarginVertical
                        )
                    } else {
                        colorDrawableBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                            itemView.top + iconMarginVertical,
                            itemView.right - iconMarginVertical,
                            itemView.bottom - iconMarginVertical
                        )
                        deleteIcon.level = 0
                    }

                    colorDrawableBackground.draw(c)

                    c.save()

                    if (dX > 0)
                        c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    else
                        c.clipRect(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )

                    deleteIcon.draw(c)

                    c.restore()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)
    }

    private fun setupStateObserver() {
        mainViewModel.uiState.observe(this, Observer { state ->
            state.navDirection?.let {
                navigator.navigate(it)
                mainViewModel.onHandleIntent(UiIntent.NavigationCompleted)
            }
            state.msgs?.let {
                Toast.makeText(requireContext(), state.msgs, Toast.LENGTH_SHORT).show()
                mainViewModel.onHandleIntent(UiIntent.ToastShown)
            }
            state.options?.let {
                if (!state.grabbingOptions) {

                    tasksRecyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE

                    mFirebaseAdapter =
                        object : FirebaseRecyclerAdapter<ToDoTask, TaskViewHolder>(it) {
                            override fun onCreateViewHolder(
                                parent: ViewGroup,
                                viewType: Int
                            ): TaskViewHolder {
                                val viewHolder = LayoutInflater.from(parent.context).inflate(
                                    R.layout.item_task, parent, false
                                )

                                return TaskViewHolder(viewHolder)
                            }

                            override fun onBindViewHolder(
                                holder: TaskViewHolder,
                                position: Int,
                                model: ToDoTask
                            ) {
                                holder.bindTask(model)
                            }
                        }

                    tasksRecyclerView.adapter = mFirebaseAdapter
                    registerFirebaseListening()

                    mFirebaseAdapter!!.registerAdapterDataObserver(object :
                        RecyclerView.AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            super.onItemRangeInserted(positionStart, itemCount)
                            mainViewModel.onHandleIntent(UiIntent.AllTaskVisible)
                        }
                    })
                    mFirebaseAdapter?.let {
                        if (it.itemCount > 0) {
                            mainViewModel.onHandleIntent(UiIntent.AllTaskVisible)
                        } else {
                            mainViewModel.onHandleIntent(UiIntent.ShowEmpty)
                        }
                    }
                }
            }
            state.loading?.let {
                when (it) {
                    true -> {
                        progressBar.visibility = View.VISIBLE
                        tasksRecyclerView.visibility = View.GONE
                        mainViewModel.initRepository(storageManager)

                        if (mainViewModel.isRepoInitialized) {
                            mainViewModel.onHandleIntent(UiIntent.ShowAllTasks)
                        } else {
                            mainViewModel.onHandleIntent(UiIntent.Loading)
                        }
                    }
                    false -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
            state.emptyData?.let {
                when (it) {
                    true -> {
                        empty_text.visibility = View.VISIBLE
                    }
                    false -> {
                        empty_text.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.onHandleIntent(UiIntent.Loading)
    }

    private fun registerFirebaseListening() {
        mFirebaseAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAdapter?.stopListening()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTask(task: ToDoTask) {
            itemView.title.text = task.title
        }
    }
}


