#!/usr//bin/ruby -w


$statepool = Array.new
$name_to_object = Hash.new

$threats = []
#$threats = IO.readlines("threat.txt")

#$threats.map! { |x|
#	AThreat.new(x)
#}

#NUMBER 1
  #WHITE
$threats.push(AThreat.new("W-BWBB+WB-BW"))
$threats.push(AThreat.new("WB-BWBB+WB-BW"))
$threats.push(AThreat.new("W-WBB+WB-BW"))
  #BLACK
$threats.push(AThreat.new("B-WBWW+BW-WB"))
$threats.push(AThreat.new("BW-WBWW+BW-WB"))
$threats.push(AThreat.new("B-BWW+BW-WB"))

#NUMBER 2
  #WHITE
$threats.push(AThreat.new("WB-W+BWB-W"))
$threats.push(AThreat.new("WB-W+BW-BW"))
$threats.push(AThreat.new("WB-W+BW-W"))
$threats.push(AThreat.new("W-W+BWB-W"))
$threats.push(AThreat.new("W-W+BWB-BW"))
$threats.push(AThreat.new("W-W+BW-W"))
$threats.push(AThreat.new("WB-W+BWBWB-W"))
$threats.push(AThreat.new("WB-W+BWBWB-BW"))
$threats.push(AThreat.new("WB-W+BWBW-W"))
$threats.push(AThreat.new("W-W+BBWB-W"))
$threats.push(AThreat.new("W-W+BWBWB-W"))
$threats.push(AThreat.new("W-W+BBWBWB-W"))
$threats.push(AThreat.new("W-W+BBWB-BW"))
$threats.push(AThreat.new("W-W+BWBWB-BW"))
$threats.push(AThreat.new("W-W+BBWBWB-BW"))
$threats.push(AThreat.new("W-W+BWBW-W"))
$threats.push(AThreat.new("W-W+BWBWBW-W"))
$threats.push(AThreat.new("W-W+BBWBW-W"))
  #BLACK
$threats.push(AThreat.new("BW-B+WBW-B"))
$threats.push(AThreat.new("BW-B+WB-BW"))
$threats.push(AThreat.new("BW-B+WB-B"))
$threats.push(AThreat.new("B-B+WBW-B"))
$threats.push(AThreat.new("B-B+WBW-WB"))
$threats.push(AThreat.new("B-B+WB-B"))
$threats.push(AThreat.new("BW-B+WBWBW-B"))
$threats.push(AThreat.new("BW-B+WBWBW-WB"))
$threats.push(AThreat.new("BW-B+WBWB-B"))
$threats.push(AThreat.new("B-B+WWBW-B"))
$threats.push(AThreat.new("B-B+WBWBW-B"))
$threats.push(AThreat.new("B-B+WWBWBW-B"))
$threats.push(AThreat.new("B-B+WWBW-WB"))
$threats.push(AThreat.new("B-B+WBWBW-WB"))
$threats.push(AThreat.new("B-B+WWBWBW-WB"))
$threats.push(AThreat.new("B-B+WBWB-B"))
$threats.push(AThreat.new("B-B+WBWBWB-B"))
$threats.push(AThreat.new("B-B+WWBWB-B"))

#NUMBER 4
  #WHITE
$threats.push(AThreat.new("W-BW+BB+W-W"))
$threats.push(AThreat.new("W-BW+BBB+W-W"))
$threats.push(AThreat.new("W-BW+BBBB+W-W"))
  #BLACK
$threats.push(AThreat.new("B-WB+WW+B-B"))
$threats.push(AThreat.new("B-WB+WWW+B-B"))
$threats.push(AThreat.new("B-WB+WWWW+B-B"))

#NUMBER 6
  #WHITE
$threats.push(AThreat.new("WWW-BW-B-B+WW"))
  #BLACK
$threats.push(AThreat.new("BBB-WB-W-W+BB"))

#NUMBER 9
  #WHITE
$threats.push(AThreat.new("WW+B-BB-BWB-WB-B+WW"))
  #BLACK
$threats.push(AThreat.new("BB+W-WW-WBW-BW-W+BB"))

#NUMBER 10
  #WHITE
$threats.push(AThreat.new("W-BWBB-B+WW-B-WW"))
$threats.push(AThreat.new("WB-BWBB-B+WW-B-WW"))
$threats.push(AThreat.new("W-WBB-B+WW-B-WW"))
  #BLACK
$threats.push(AThreat.new("B-WBWW-W+BB-W-BB"))
$threats.push(AThreat.new("BW-WBWW-W+BB-W-BB"))
$threats.push(AThreat.new("B-BWW-W+BB-W-BB"))

#NUMBER 11
  #WHITE
$threats.push(AThreat.new("W-W+B-B+WW-B-WW"))
  #BLACK
$threats.push(AThreat.new("B-B+W-W+BB-W-BB"))

#NUMBER 12
	#WHITE
$threats.push(AThreat.new("W-WB+WW-WB-WB+W-W"))
$threats.push(AThreat.new("WB-WB+WW-WB-WB+W-W"))
$threats.push(AThreat.new("WB-BWB+WW-WB-WB+W-W"))
	#BLACK
$threats.push(AThreat.new("B-BW+BB-BW-BW+B-B"))
$threats.push(AThreat.new("BW-BW+BB-BW-BW+B-B"))
$threats.push(AThreat.new("BW-WBW+BB-BW-BW+B-B"))

#NUMBER 13
	#WHITE
$threats.push(AThreat.new("BWBBW"))
	#BLACK
$threats.push(AThreat.new("WBWWB"))

#NUMBER 14
	#WHITE
$threats.push(AThreat.new("W-WBBW-BBWB-B+W-W"))
$threats.push(AThreat.new("W-BWBBW-BBWB-B+W-W"))
$threats.push(AThreat.new("WB-BWBBW-BBWB-B+W-W"))
	#BLACK
$threats.push(AThreat.new("B-BWWB-WWBW-W+B-B"))
$threats.push(AThreat.new("B-WBWWB-WWBW-W+B-B"))
$threats.push(AThreat.new("BW-WBWWB-WWBW-W+B-B"))

#NUMBER 15
	#WHITE
$threats.push(AThreat.new("W-W+B-BWBB-WB+W-W"))
	#BLACK
$threats.push(AThreat.new("B-B+W-WBWW-BW+B-B"))

#NUMBER 16
	#WHITE
$threats.push(AThreat.new("W-W+B-BWBB-WB+W-W"))
$threats.push(AThreat.new("W-W+B-BWBBW"))
	#BLACK
$threats.push(AThreat.new("B-B+W-WBWW-BW+B-B"))
$threats.push(AThreat.new("B-B+W-WBWWB"))

#NUMBER 19
	#WHITE
$threats.push(AThreat.new("W-W+B-BWBBW-BW"))
$threats.push(AThreat.new("WB-W+B-BWBBWBW"))
$threats.push(AThreat.new("W-W+B-BWBBW-W"))
$threats.push(AThreat.new("WB-W+B-BWBBW-W"))
	#BLACK
$threats.push(AThreat.new("B-B+W-WBWWB-WB"))
$threats.push(AThreat.new("BW-B+W-WBWWBWB"))
$threats.push(AThreat.new("B-B+W-WBWWB-B"))
$threats.push(AThreat.new("BW-B+W-WBWWB-B"))

#NUMBER 20
	#WHITE
$threats.push(AThreat.new("W-BW+BBW-W"))
$threats.push(AThreat.new("W-BW+BBWB-W"))
$threats.push(AThreat.new("W-BW+BBWB-BW"))
$threats.push(AThreat.new("W-BW+BBWBW-W"))
	#BLACK
$threats.push(AThreat.new("B-WB+WWB-B"))
$threats.push(AThreat.new("B-WB+WWBW-B"))
$threats.push(AThreat.new("B-WB+WWBW-WB"))
$threats.push(AThreat.new("B-WB+WWBWB-B"))

#NUMBER 21
	#WHITE
$threats.push(AThreat.new("W-W+B-BW-BW"))
$threats.push(AThreat.new("WB-W+B-BW-BW"))
	#BLACK
$threats.push(AThreat.new("B-B+W-WB-WB"))
$threats.push(AThreat.new("BW-B+W-WB-WB"))

#NUMBER 22
	#WHITE
$threats.push(AThreat.new("W-WBB-BWB-W"))
$threats.push(AThreat.new("W-WBBB-BWB-W"))
$threats.push(AThreat.new("W-BWBB-BWB-W"))
$threats.push(AThreat.new("WB-BWBB-BWB-W"))
$threats.push(AThreat.new("WB-WBB-BWB-W"))
$threats.push(AThreat.new("WB-WBBB-BWB-W"))
$threats.push(AThreat.new("WB-BWBBB-B-WB-W"))
$threats.push(AThreat.new("W-BWBBB-BWB-W"))
	#BLACK
$threats.push(AThreat.new("B-BWW-WBW-B"))
$threats.push(AThreat.new("B-BWWW-WBW-B"))
$threats.push(AThreat.new("B-WBWW-WBW-B"))
$threats.push(AThreat.new("BW-WBWW-WBW-B"))
$threats.push(AThreat.new("BW-BWW-WBW-B"))
$threats.push(AThreat.new("BW-BWWW-WBW-B"))
$threats.push(AThreat.new("BW-WBWWW-W-BW-B"))
$threats.push(AThreat.new("B-WBWWW-WBW-B"))

#NUMBER 24
	#WHITE
$threats.push(AThreat.new("WB-BW+B-BB+WB-BW"))
	#BLACK
$threats.push(AThreat.new("BW-WB+W-WW+BW-WB"))

#NUMBER 25A
	#WHITE
$threats.push(AThreat.new("W+B-WBBB+WB-BW"))
$threats.push(AThreat.new("W+B-WBBB+WB-W"))
	#BLACK
$threats.push(AThreat.new("B+W-BWWW+BW-WB"))
$threats.push(AThreat.new("B+W-BWWW+BW-W"))

#NUMBER 25B
	#WHITE
$threats.push(AThreat.new("W-BWBBB+WB-BW"))
$threats.push(AThreat.new("W-BWBBB+WB-W"))
$threats.push(AThreat.new("W-WBBB+WB-W"))
$threats.push(AThreat.new("W-WBBB+WB-BW"))
$threats.push(AThreat.new("WB-BWBBB+WB-BW"))
$threats.push(AThreat.new("WB-BWBBB+WB-W"))
	#BLACK
$threats.push(AThreat.new("B-WBWWW+BW-WB"))
$threats.push(AThreat.new("B-WBWWW+BW-B"))
$threats.push(AThreat.new("B-BWWW+BW-B"))
$threats.push(AThreat.new("B-BWWW+BW-WB"))
$threats.push(AThreat.new("BW-WBWWW+BW-WB"))
$threats.push(AThreat.new("BW-WBWWW+BW-W"))

#NUMBER 26
	#WHITE
$threats.push(AThreat.new("WWB-BB-WW+B-W"))
	#BLACK
$threats.push(AThreat.new("BBW-WW-BB+W-B"))

#NUMBER 27
	#WHITE
$threats.push(AThreat.new("W-WBB-W+B-W"))
$threats.push(AThreat.new("W-BWBB-W+B-W"))
$threats.push(AThreat.new("WB-WBB-W+B-W"))
$threats.push(AThreat.new("WB-BWBB-W+B-W"))
$threats.push(AThreat.new("W-WBBB-W+B-W"))
$threats.push(AThreat.new("W-BWBBB-W+B-W"))
$threats.push(AThreat.new("WB-WBBB-W+B-W"))
$threats.push(AThreat.new("WB-BWBBB-W+B-W"))
	#BLACK
$threats.push(AThreat.new("B-BWW-B+W-B"))
$threats.push(AThreat.new("B-WBWW-B+W-B"))
$threats.push(AThreat.new("BW-BWW-B+W-B"))
$threats.push(AThreat.new("BW-WBWW-B+W-B"))
$threats.push(AThreat.new("B-BWWW-B+W-B"))
$threats.push(AThreat.new("B-WBWWW-B+W-B"))
$threats.push(AThreat.new("BW-BWWW-B+W-B"))
$threats.push(AThreat.new("BW-WBWWW-B+W-B"))

#NUMBER 28
	#WHITE
$threats.push(AThreat.new("W-BWW+BWB-B+W-W"))
$threats.push(AThreat.new("W-WW+BWB-B+W-W"))
$threats.push(AThreat.new("WB-BWW+BWB-B+W-W"))
	#BLACK
$threats.push(AThreat.new("B-WBB+WBW-W+B-B"))
$threats.push(AThreat.new("B-BB+WBW-W+B-B"))
$threats.push(AThreat.new("BW-WBB+WBW-W+B-B"))

#NUMBER 29
	#WHITE
$threats.push(AThreat.new("WB-BWW+BWB-BBW-W"))
$threats.push(AThreat.new("W-WW+BWB-BBW-W"))
$threats.push(AThreat.new("W-BWW+BWB-BBW-W"))
$threats.push(AThreat.new("WB-WW+BWB-BBW-W"))
$threats.push(AThreat.new("W-WW+BWBWB-BBW-W"))
$threats.push(AThreat.new("W-BWW+BWBWB-BBW-W"))
	#BLACK
$threats.push(AThreat.new("BW-WBB+WBW-WWB-B"))
$threats.push(AThreat.new("B-BB+WBW-WWB-B"))
$threats.push(AThreat.new("B-WBB+WBW-WWB-B"))
$threats.push(AThreat.new("BW-BB+WBW-WWB-W"))
$threats.push(AThreat.new("B-BB+WBWBW-WWB-B"))
$threats.push(AThreat.new("B-WBB+WBWBW-WWB-B"))

#NUMBER 30
	#WHITE
$threats.push(AThreat.new("W-WBB-W-B+WB+WB"))
$threats.push(AThreat.new("W-BWBB-W-B+WB+WB"))
$threats.push(AThreat.new("WB-WBB-W-B+WB+WB"))
	#BLACK
$threats.push(AThreat.new("B-BWW-B-W+BW+BW"))
$threats.push(AThreat.new("B-WBWW-B-W-BW+BW"))
$threats.push(AThreat.new("BW-BWW-B-W+BW+BW"))

#NUMBER 31
	#WHITE
$threats.push(AThreat.new("W-W+B-W-B+WB+WB"))
	#BLACK
$threats.push(AThreat.new("B-B+W-B-W+BW+BW"))

#NUMBER 32
	#WHITE
$threats.push(AThreat.new("W+B-WW-B+WB+WB"))
	#BLACK
$threats.push(AThreat.new("B+W-BB-W+BW+BW"))




head = State.new(Array.new($threats.size,0))
$statepool << head
$name_to_object[head.name] = head
$done_states = Array.new

def advance(current, edge)
	temp = current.copy
	size = $threats.size
	0.upto(size-1) { |threat_num|
		threat_name_counter = temp.name[threat_num]
		if($threats[threat_num].name[threat_name_counter] == edge[0])
			if(threat_name_counter == $threats[threat_num].name.size-1)
				current.add_match($threats[threat_num].name, edge)
				temp.change(threat_num, match_substr($threats[threat_num].name))
			else
				temp.change(threat_num, temp.name[threat_num]+1)
			end
		else
			temp.change(threat_num, match_substr2(edge, $threats[threat_num].name, threat_name_counter))
		end
	}
	current.go_to[edge]=temp.name
	object = $name_to_object[temp.name]
	if(not object)
		temp.edge = edge
		$statepool << temp
		$name_to_object[temp.name] = temp
	end
end


while($statepool.size > 0)
	current = $statepool.pop
	advance(current, "B")
	advance(current, "W")
	advance(current, "+")
	advance(current, "-")

	$done_states << current
end

a=Hash.new
count=0
$done_states.each { |state|
	a[state.name.join("")] = count
	count+=1
}

puts("switch (state) {")

$done_states.each { |state|
	puts("  case " + a[state.name.join("")].to_s + ":")
	puts("    switch (border[i]) {")
	puts("      case 'W':")
	print("        state=" + a[state.go_to["W"].join("")].to_s + ";")
 	if state.match_white.join("").length > 0 then print(" // " + state.match_white.join("")); end
	puts
	puts("        break;")
	puts("      case 'B':")
	print("        state=" + a[state.go_to["B"].join("")].to_s + ";");
	if state.match_black.join("").length > 0 then print(" // " + state.match_black.join("")); end
	puts
	puts("        break;")
	if state.edge != "-" and state.edge != "+"
		puts("      case '+':")
		puts("        state=" + a[state.go_to["+"].join("")].to_s + ";")
		puts("        break;")
		puts("      case '-':")
		puts("        state=" + a[state.go_to["-"].join("")].to_s + ";")
		puts("        break;")
	end
	puts("      default:")
	puts("        /* This should never happen */")
	puts("        assert(0);")
	puts("        break;")
	puts("    }")
	puts("    break;")
}
puts("  default:")
puts("  /* This should never happen */")
puts("    assert(0);")
puts("    break;")
puts("}")

BEGIN {
class AThreat
	attr_reader :name
	def initialize(name)
		@name=name
	end
end

class State
	attr_reader :match_white, :match_black, :match_minus, :match_plus
	attr_accessor :name, :go_to, :edge
	def initialize(name)
		@name=name
		@edge=nil
		@match_white=Array.new
		@match_black=Array.new
		@match_minus=Array.new
		@match_plus=Array.new
		@go_to = Hash.new
	end
	def add_match(threat, colour)
		case colour
			when "W"
				@match_white << threat
			when "B"
				@match_black << threat
			when "-"
				@match_minus << threat
			when "+"
				@match_plus << threat
			else
				puts "got #{colour} in line ABECAT"
				exit
		end
	end	
	def change(which, number)
		@name[which] = number
	end
	def copy
		return State.new(@name.dup)
	end
end

def match_substr(my_match)
	begin
		forward=String.new(my_match)
		backward=String.new(my_match)
		forward.slice!(0)
		backward.slice!(-1)

		while(forward.size > 0 && (forward != backward))
			forward.slice!(0)
			backward.slice!(-1)
		end
		return forward.size
	rescue
		return 0
	end
end

def match_substr2(edge, name, counter)
	return 0 if counter==0
	begin
		forward=name.slice(0, counter)+edge
		backward=name.slice(0, counter+1)
		forward.slice!(0)
		backward.slice!(-1)

		while(forward.size > 0 && (forward != backward))
			forward.slice!(0)
			backward.slice!(-1)
		end
		return forward.size
	rescue
		return 0
	end
end

class Array
	def dup
		a=Array.new
		self.each { |e|
			a << e
		}
		return a
	end
end
}




