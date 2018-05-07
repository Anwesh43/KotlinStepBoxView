package com.example.stepboxview

/**
 * Created by anweshmishra on 07/05/18.
 */

import android.content.*
import android.view.*
import android.graphics.*

val SB_NODES : Int = 10
class StepBoxView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += dir * 0.1f
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate (updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SBNode (var i : Int = 0, val state : State = State()) {
        var next : SBNode? = null
        var prev : SBNode? = null
        init {
            this.addNeighbor()
        }

        fun addNeighbor() {
            if (i  < SB_NODES - 1) {
                val node : SBNode = SBNode(i+1)
                next = node
                node.prev = this
            }
        }

        fun getNext(dir : Int, cb : () -> Unit) : SBNode {
            var curr : SBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val size : Float = (w)/ SB_NODES
            canvas.save()
            canvas.translate(size * i, h - (i +1) * size)
            canvas.drawRect(RectF(0f, 0f, size * state.scale, size), paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class StepBox (var i : Int) {

        var dir : Int = 1

        private var curr : SBNode = SBNode()

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                this.curr = this.curr.getNext(dir) {
                    this.dir *= -1
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : StepBoxView) {

        private val stepBox : StepBox = StepBox(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            paint.color = Color.parseColor("#e74c3c")
            stepBox.draw(canvas, paint)
            animator.animate {
                stepBox.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            stepBox.startUpdating {
                animator.start()
            }
        }
    }
}