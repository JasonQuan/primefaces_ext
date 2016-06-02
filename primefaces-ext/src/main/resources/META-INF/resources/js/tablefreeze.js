(function($) {
	var data_key = "scrollbar";

	var $bars = $("<div/>").addClass('scrollbar');
	var $vert = $bars.clone().addClass('vertical');
	var $horz = $bars.clone().addClass('horizontal');

	// TODO: add classes
	var defaults = {
		h : 1,
		v : 1,
		autoHide : 1,
		grow : 0,
		growBy : 5
	};

	// var dragJs = 'drag.js'; //location of drag.js

	/**
	 * Scrollbar
	 * 
	 * @param {type}
	 *            el - element that needs scrollbars
	 * @param {array}
	 *            opts - options
	 */
	$.scrollbar = function(el, opts) {
		this.o = $.extend({}, defaults, opts);
		this.$t = $(el);
		this.$p = this.$t.parent();
		this.dragging = false;

		if (this.o.h) {
			this.$h = $horz.clone();
			this.$p.append(this.$h);
		}

		if (this.o.v) {
			this.$v = $vert.clone();
			this.$p.append(this.$v);
		}

		this.reset(); // set height and width

		// add event listener to parent
		if (this.o.autoHide) {
			this.$p.hover($.proxy(this, 'show'), $.proxy(this, 'hide'));
		} else {
			this.show();
		}

		if (this.o.grow) {
			var growBy = this.o.growBy;
			if (this.$v)
				this.$v.mouseover(function() {
					$(this).css('width', '+=' + growBy);
				}).mouseout(function() {
					$(this).css('width', '-=' + growBy);
				});
			if (this.$h)
				this.$h.mouseover(function() {
					$(this).css('height', '+=' + growBy);
				}).mouseout(function() {
					$(this).css('height', '-=' + growBy);
				});
		}

		// allow drag if file exists
		// $.loadJs(dragJs, $.proxy(implementDrag, this) );
		implementDrag.call(this);

	};

	function implementDrag() {
		if (this.$v) {
			this.$v.drag({
				dir : 1,
				max : this.$p.height() - this.h
			});
			this.$v.on('drag::dragStart', $.proxy(dragStart, this)).on(
					'drag::dragStop', $.proxy(dragStop, this)).on(
					'drag::dragging', $.proxy(draggingY, this));
		}

		if (this.$h) {
			this.$h.drag({
				dir : 0,
				max : this.$p.width() - this.w
			});
			this.$h.on('drag::dragStart', $.proxy(dragStart, this)).on(
					'drag::dragStop', $.proxy(dragStop, this)).on(
					'drag::dragging', $.proxy(draggingX, this));
		}
	}

	function dragStart() {
		this.dragging = true;
	}
	;
	function dragStop() {
		this.dragging = false;
	}
	;
	function draggingX(ev, dx, dy) {
		var pdx = dx / (this.$p.width() - this.w);
		this.$t.trigger('scrollbar::dragX', [ pdx ]);
	}
	;

	function draggingY(ev, dx, dy) {
		var pdy = dy / (this.$p.height() - this.h);
		this.$t.trigger('scrollbar::dragY', [ pdy ]);
	}
	;

	/**
	 * Shows the scrollbar
	 */
	$.scrollbar.prototype.show = function() {
		if (this.o.v)
			this.$v.animate({
				opacity : 1
			}, 50);
		if (this.o.h)
			this.$h.animate({
				opacity : 1
			}, 50);
		this.$t.trigger('scrollbar::show', [ this.$v, this.$h ]);
	};

	/**
	 * Hides the scrollbar
	 */
	$.scrollbar.prototype.hide = function() {
		if (this.dragging)
			return;
		if (this.o.v)
			this.$v.animate({
				opacity : 0
			}, 50);
		if (this.o.h)
			this.$h.animate({
				opacity : 0
			}, 50);
		this.$t.trigger('scrollbar::hide', [ this.$v, this.$h ]);
	};

	$.scrollbar.prototype.reset = function() {
		var p = this.$p.css('position');
		if (!p || p == 'static')
			this.$p.css('position', 'relative');

		if (this.o.v) {
			var h = this.$p.height(), hi = this.$t.height();
			if (hi > h) {
				this.h = h / hi * h;
				this.$v.css('height', this.h);
			}
		}

		if (this.o.h) {
			var w = this.$p.width(), wi = this.$t.width();
			if (wi > w) {
				this.w = w / wi * w;
				this.$h.css('width', this.w);
			}
		}
	};

	/**
	 * Updates the scrollbars.
	 * 
	 * @param {double}
	 *            x - horizontal % (integer from 0-1)
	 * @param {double}
	 *            y - vertical % (integer from 0-1)
	 */
	$.scrollbar.prototype.update = function(x, y) {
		if (y < 0)
			y = 0;
		if (x < 0)
			x = 0;
		if (y > 1)
			y = 1;
		if (x > 1)
			x = 1;

		if (this.o.v && this.$v) {
			var t = (this.$p.height() - this.h) * y;
			this.$v.css({
				top : t
			});
		}

		if (this.o.h && this.$h) {
			var l = (this.$p.width() - this.w) * x;
			this.$h.css({
				left : l
			});
		}
	};

	$.fn.scrollbar = function(opts) {
		if ($(this).length != 1) {
			var arr = [];
			$(this).each(function() {
				arr.push($(this).scrollbar(this, opts));
			});
			return arr;
		}

		var l = $(this).data(data_key);
		if (l instanceof $.scrollbar)
			return l;

		l = new $.scrollbar(this, opts);
		$(this).data(data_key, l);
		return l;
	};

})(jQuery);
(function($) {
	var data_key = "drag";

	var defaults = {
		dir : null,
		min : 0,
		max : null
	};

	$.drag = function(el, opts) {
		this.o = $.extend({}, defaults, opts);
		this.$t = $(el);
		this.ix = this.iy = this.sx = this.sy = 0; // initial x,y and scroll
		// x,y
		this.md = false;
		var p = this.$t.css('position');
		if (!p || p == "static")
			this.$t.css('position', 'relative');

		var $p = this.$t.parent();
		p = $p.css('position');
		if (!p || p == 'static')
			$p.css('position', 'relative');

		if (!this.o.dir && this.o.dir !== 0) {
			this.v = true;
			this.h = true;
		} else {
			this.h = this.o.dir == 0 || this.o.dir == "h";
			this.v = this.o.dir == 1 || this.o.dir == "v";
		}

		if (!this.v && !this.h)
			return;

		this.$t.mousedown($.proxy(this, 'mousedown'));
		$(window).mouseup($.proxy(this, 'mouseup')).mousemove(
				$.proxy(this, 'mousemove'));
	};

	$.drag.prototype.mousedown = function(e) {
		this.md = true;
		this.p = this.$t.position(); // starting position

		getCurrentXY.call(this, e);
		this.$t.trigger('drag::dragStart', this);

		e.preventDefault();
		e.stopPropagation();
	};

	$.drag.prototype.mouseup = function() {
		this.md = false;
		this.$t.trigger('drag::dragStop', this);
	};

	$.drag.prototype.mousemove = function(e) {
		if (!this.md)
			return;

		var m = getCurrentXY.call(this, e, true);
		var mX, MX, mY, MY;

		if (this.o.min === null) {

		} else if (this.o.min instanceof Array) {
			mX = this.o.min[0];
			mY = this.o.min[1];
		} else {
			mX = mY = this.o.min;
		}

		if (this.o.max === null) {
			// do nothing
		} else if (this.o.max instanceof Array) {
			MX = this.o.max[0];
			MY = this.o.max[1];
		} else {
			MX = MY = this.o.max;
		}

		var dy = 0;
		var dx = 0;
		if (this.v) {
			dy = m[1] - this.iy + m[3] - this.sy + this.p.top;
			if (dy < mY)
				dy = mY;
			else if (MY && dy > MY)
				dy = MY;
			this.$t.css('top', dy + "px");
		}

		if (this.h) {
			dx = m[0] - this.ix + m[2] - this.sx + this.p.left;
			if (dx < mX)
				dx = mX;
			else if (MX && dx > MX)
				dx = MX;
			this.$t.css('left', dx + "px");
		}

		this.$t.trigger('drag::dragging', [ dx, dy ]);
	};

	function getCurrentXY(e, ret) {
		var x = e.pageX, y = e.pageY;
		var sy = this.$t.scrollTop();
		var sx = this.$t.scrollLeft();

		if (!ret) {
			this.ix = x;
			this.iy = y;
			this.sx = sx;
			this.sy = sy;
		} else {
			return [ x, y, sx, sy ];
		}
	}

	$.fn.drag = function(opts) {
		if ($(this).size() != 1) {
			var arr = [];
			$(this).each(function() {
				arr.push($(this).drag(opts));
			});
			return arr;
		}

		var l = $(this).data(data_key);
		if (l instanceof $.drag)
			return l;

		l = new $.drag(this, opts);
		return l;
	};

})(jQuery);
(function($) {

	var data_key = "tableFreeze";

	var defaults = {
		'cols' : 1,
		'threshold-x' : 35,
		'threshold-y' : 35,
		'scrollbar' : 1
	};

	// var keys = [37, 38, 39, 40];

	$.tableFreeze = function(tbl, options) {
		this.$this = $(tbl); // IMPORTANT!
		this.options = $.extend({}, defaults, options);

		this.x = 0;
		this.y = 0;

		this.hdr = []; // cache to store complex header architecture!

		this.h = this.$this.height();
		this.w = this.$this.width();

		// Set scrolling event handlers
		var __this = this;
		this.$this.on('mousewheel', function(e) {
			var x, y;

			if (e.originalEvent.wheelDeltaX || e.originalEvent.wheelDeltaY) {
				x = e.originalEvent.wheelDeltaX / -40;
				y = e.originalEvent.wheelDeltaY;
			} else {
				x = e.originalEvent.deltaX;
				y = e.originalEvent.deltaY;
			}

			__this.x += x;
			__this.y += y;

			if (Math.abs(__this.x) > __this.options['threshold-x']) {
				(__this.x > 0) ? setFreezeX.call(__this, 0) : setFreezeX.call(
						__this, 1);
				__this.x = 0;
			}

			if (Math.abs(__this.y) > __this.options['threshold-y']) {
				(__this.y > 0) ? setFreezeY.call(__this, 1) : setFreezeY.call(
						__this, 0);
				__this.y = 0;
			}

			e.preventDefault();
			e.stopPropagation();
		});

		if (!this.options.scrollbar)
			return; // done!

		var obj = $.isPlainObject(this.options.scrollbar) ? this.options.scrollbar
				: null;
		this.sb = this.$this.scrollbar(obj);

		var $p = this.$this.parent(), wi = $p.width(), hi = $p.height();
		var hth = this.$this.find('thead').height();

		// Number of cols and rows (to hide) for percentages.
		this.numRows = this.$this.find('tbody tr').length;
		this.numCols = this.$this.find('tbody tr').eq(0).find('td').length;

		// Get the number of rows currently showing
		this.numRows -= (hi - hth) / (this.h - hth) * this.numRows;
		this.numCols -= wi / this.w * this.numCols;

		this.hiddenRows = 0;
		this.hiddenCols = 0;

		this.$this.on('scrollbar::dragX', function(e, amt) {
			var diff = Math.ceil(amt * __this.numCols) - __this.hiddenCols;
			if (diff == 0)
				return;

			var abs = Math.abs(diff);
			for (var i = 0; i < abs; i++) {
				setFreezeX.call(__this, diff > 0 ? 0 : 1);
			}
		}).on('scrollbar::dragY', function(e, amt) {
			var diff = Math.ceil(amt * __this.numRows) - __this.hiddenRows;
			if (diff == 0)
				return;

			var abs = Math.abs(diff);
			for (var i = 0; i < abs; i++) {
				setFreezeY.call(__this, diff > 0 ? 0 : 1);
			}
		});

	};

	$.tableFreeze.prototype.$get = function() {
		return this.$this;
	};

	$.tableFreeze.prototype.set = function(cols/* , rows */) {
		this.options.cols = cols; // set number of cols.
		this.$this.find('.hidden').removeClass('hidden'); // show everything
		this.hiddenCols = 0;
		this.hiddenRows = 0;
		this.resetScrollbars();
		// this.options.rows = rows;
	};

	/**
	 * Update scroll bars
	 */
	$.tableFreeze.prototype.updateScrollbars = function() {
		/*
		 * var $p = this.$this.parent(), w=$p.width(), h=$p.height(), wi =
		 * this.$this.width(), hi = this.$this.height();
		 * 
		 * if( hi < h ) hi = h; if( wi < w ) wi = w;
		 * 
		 * this.sb.update( 1-(w-wi)/(w-this.w) , 1-(h-hi)/(h-this.h) );
		 */

		this.sb.update(this.hiddenCols / this.numCols, this.hiddenRows
				/ this.numRows);
	};

	$.tableFreeze.prototype.resetScrollbars = function() {
		this.h = this.$this.height();
		this.w = this.$this.width();
		this.sb.reset();
		this.updateScrollbars();
	};

	/**
	 * Freezes the rows and columns by showing/hiding when scrolling; need to
	 * determine when to stop hiding and when to show;
	 * 
	 * @param dir -
	 *            direction of freeze; 0 - x ; 1 - y;
	 * @param amt -
	 *            0 - left/up; 1 - right/down;
	 * 
	 * function setFreeze(dir, amt){ (dir)? setFreezeY.call(this, amt) :
	 * setFreezeX.call(this, amt); this.updateScrollbars(); }
	 */

	function setFreezeX(amt) {
		// var hidden = [];

		// Go right
		if (amt) {
			// Need to deal with grouped th;
			this.$this.find('thead tr').each(
					function() {
						/* hidden.push( */$(this).find('th.hidden').last()
								.removeClass('hidden') /* ) */;
					});
			this.$this.find('tbody tr').each(
					function() {
						/* hidden.push( */$(this).find('td.hidden').last()
								.removeClass('hidden') /* ) */;
					});
			this.$this.trigger('tableFreeze::unfrozen', 0); // [0, hidden]);
			this.hiddenCols--;

			// Go left if not isStop
		} else if (!isStopX.call(this)) {
			var col = this.options.cols;

			this.$this.find('thead tr').each(function() {
				var curr = $(this).find('th:not(.hidden)').eq(col);
				var colspan = curr.attr('colspan');
				if (colspan && parseInt(colspan) > 1) {
					curr.attr('colspan', parseInt(colspan) - 1);
				} else {
					curr.addClass('hidden');
					// hidden.push( curr );
				}
			});

			this.$this.find('tbody tr').each(
					function() {
						/* hidden.push( */$(this).find('td:not(.hidden)').eq(
								col).addClass('hidden')/* ) */;
					});
			this.$this.trigger('tableFreeze::frozen', 0); // [0, hidden]);
			this.hiddenCols++;
		}
		this.updateScrollbars();
	}

	function setFreezeY(amt) {
		// var t = [];

		// Go down
		if (amt) {
			/* t.push( */this.$this.find('tbody tr.hidden').last()
					.removeClass('hidden') /* ) */;
			this.$this.trigger('tableFreeze::unfrozen', 1); // [1, t]);
			this.hiddenRows--;
			// Go up if not isStop
		} else if (!isStopY.call(this)) {
			/* t.push( */this.$this.find('tbody tr:not(.hidden)').first()
					.addClass('hidden') /* ) */;
			this.$this.trigger('tableFreeze::frozen', 1); // [1, t]);
			this.hiddenRows++;
		}
		this.updateScrollbars();
	}

	/**
	 * Determines when to stop freezing (when width of table == width of
	 * container)
	 */
	function isStopX() {
		return this.$this.width() + 15 <= this.$this.parent().width(); // Doesn't
		// work
		// in
		// Safari!
	}

	/**
	 * Determines when to stop freezing ( when height of table == height of
	 * container)
	 */
	function isStopY() {
		return this.$this.height() + 15 <= this.$this.parent().height();
	}

	/**
	 * Freezes columns and rows on a table.
	 * 
	 * @param {type}
	 *            options
	 * @returns {Array}
	 */
	$.fn.tableFreeze = function(options) {
		if ($(this).length != 1) {
			var arr = [];
			$(this).each(function() {
				arr.push($(this).tableFreeze(this, options));
			});
			return arr;
		}

		var l = $(this).data(data_key);
		if (l instanceof $.tableFreeze)
			return l;

		l = new $.tableFreeze(this, options);
		$(this).data(data_key, l);
		return l;
	};

})(jQuery);
/**
 * freeze Rows in testing function
 * 
 * @param {type}
 *            size
 * @returns {undefined}
 */
function freezeRows(size) {
	var $dt = $('.ui-datatable-tablewrapper table');
	var $dtHead = $dt.find('thead');
	var freezeTop = $dtHead.height() + $dtHead.offset().top;

	var $dtBody = $dt.find('tbody');
	for (var i = 0; i < size; i++) {
		var $tr = $dtBody.find('tr').eq(i);
		$tr.css({
			position : 'fixed',
			top : freezeTop,
			'z-index' : 999999
		});
		freezeTop += $tr.height();
	}
}
